package com.highconcurrency.ticketing.infrastructure.database.redis;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusType;
import com.highconcurrency.ticketing.domain.reservation.ReservationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("redis")
@RequiredArgsConstructor
public class RedisReservationQueueRepository implements ReservationQueueRepository {

    private static final int MAX_PERMITTED_SIZE = 10000;
    private static final int MAX_QUEUE_SIZE = 100000;
    private static final String KEY_PREFIX = "queue:";
    private static final String SEQ = ":seq";
    private static final String ALLOWED = ":allowed";
    private static final String WAITING = ":waiting";
    private static final RedisScript<List> ENTER_QUEUE_SCRIPT = RedisScript.of(
            """
                    local userId = ARGV[1]
                    local maxPermittedSize = tonumber(ARGV[2])
                    local maxQueueSize = tonumber(ARGV[3])
                    
                    local allowedSeq = redis.call('ZSCORE', KEYS[2], userId)
                    if allowedSeq then
                        return {1, 0}
                    end
                    
                    local waitingSeq = redis.call('ZSCORE', KEYS[3], userId)
                    if waitingSeq then
                        local rank = redis.call('ZRANK', KEYS[3], userId)
                        return {2, rank + 1}
                    end
                    
                    local seq = redis.call('INCR', KEYS[1])
                    
                    if seq > maxQueueSize then
                        redis.call('DECR', KEYS[1])
                        return {-1, 0}
                    end
                    
                    if seq <= maxPermittedSize then
                        redis.call('ZADD', KEYS[2], seq, userId)
                        return {1, 0}
                    end
                    
                    redis.call('ZADD', KEYS[3], seq, userId)
                    local rank = redis.call('ZRANK', KEYS[3], userId)
                    
                    return {2, rank + 1}
                    """,
            List.class
    );

    private final StringRedisTemplate redisTemplate;

    private static String getSeqKey(Long concertId) {
        return KEY_PREFIX + concertId + SEQ;
    }

    private static String getAllowedKey(Long concertId) {
        return KEY_PREFIX + concertId + ALLOWED;
    }

    private static String getWaitingKey(Long concertId) {
        return KEY_PREFIX + concertId + WAITING;
    }

    @Override
    public ReservationQueueStatusResponse enter(Long concertId, Long userId) {
        List<?> result = redisTemplate.execute(
                ENTER_QUEUE_SCRIPT,
                List.of(getSeqKey(concertId), getAllowedKey(concertId), getWaitingKey(concertId)),
                String.valueOf(userId),
                String.valueOf(MAX_PERMITTED_SIZE),
                String.valueOf(MAX_QUEUE_SIZE)
        );

        if (result == null) {
            throw new HighConcurrencyTicketingException(ErrorCode.INTERNAL_SERVER_ERROR, "대기열 진입 처리에 실패했습니다.");
        }

        RedisReservationQueueEnterResult enterResult = RedisReservationQueueEnterResult.from(result);
        enterResult.validateQueueNotFull();

        return enterResult.isPermitted() ?
                ReservationQueueStatusResponse.builder()
                        .concertId(concertId)
                        .userId(userId)
                        .status(ReservationQueueStatusType.PERMITTED)
                        .seq(0)
                        .build()
                :
                ReservationQueueStatusResponse.builder()
                        .concertId(concertId)
                        .userId(userId)
                        .status(ReservationQueueStatusType.WAITING)
                        .seq(enterResult.getWaitingRank())
                        .build();
    }

    @Override
    public ReservationQueueStatusResponse getStatus(Long concertId, Long userId) {
        String userIdValue = String.valueOf(userId);

        Double allowedSeq = redisTemplate.opsForZSet().score(getAllowedKey(concertId), userIdValue);
        if (allowedSeq != null) {
            return ReservationQueueStatusResponse.builder()
                    .concertId(concertId)
                    .userId(userId)
                    .status(ReservationQueueStatusType.PERMITTED)
                    .seq(0)
                    .build();
        }

        Long rank = redisTemplate.opsForZSet().rank(getWaitingKey(concertId), userIdValue);
        if (rank == null) {
            throw new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 사용자가 대기열에 없습니다.");
        }

        return ReservationQueueStatusResponse.builder()
                .concertId(concertId)
                .userId(userId)
                .status(ReservationQueueStatusType.WAITING)
                .seq((int) (rank + 1))
                .build();
    }

    @Override
    public boolean isPermitted(Long concertId, Long userId) {
        return redisTemplate.opsForZSet().score(getAllowedKey(concertId), String.valueOf(userId)) != null;
    }

    @Override
    public Optional<Long> permitNextWaitingUser(Long concertId) {
        ZSetOperations.TypedTuple<String> user = redisTemplate.opsForZSet()
                .popMin(getWaitingKey(concertId));

        if (user == null) {
            return Optional.empty();
        }

        redisTemplate.opsForZSet().add(getAllowedKey(concertId), user.getValue(), user.getScore());

        return Optional.of(Long.valueOf(user.getValue()));
    }

    @Override
    public boolean leaveAndWasPermitted(Long concertId, Long userId) {
        String userIdValue = String.valueOf(userId);

        Long removedAllowedCount = redisTemplate.opsForZSet().remove(getAllowedKey(concertId), userIdValue);
        if (removedAllowedCount != null && removedAllowedCount == 1L) {
            return true;
        }

        Long removedWaitingCount = redisTemplate.opsForZSet().remove(getWaitingKey(concertId), userIdValue);
        if (removedWaitingCount != null && removedWaitingCount == 1L) {
            return false;
        }

        throw new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 사용자가 대기열에 없습니다.");
    }
}
