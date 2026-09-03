package com.highconcurrency.ticketing.infrastructure.database.java;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusType;
import com.highconcurrency.ticketing.domain.reservation.ReservationQueueRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("java")
public class JavaReservationQueueRepository implements ReservationQueueRepository {

    private static final int MAX_PERMITTED_SIZE = 10000;
    private static final int MAX_QUEUE_SIZE = 100000;

    private final ConcurrentHashMap<Long, AtomicInteger> nextSeqByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> lastPermittedSeqByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, WaitingSeqTree> waitingSeqByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ConcertUser, Integer> seqByConcertAndUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ConcertSeq, Long> userIdByConcertAndSeq = new ConcurrentHashMap<>();

    @Override
    public ReservationQueueStatusResponse enter(Long concertId, Long userId) {
        ConcertUser concertUser = new ConcertUser(concertId, userId);

        seqByConcertAndUser.computeIfAbsent(concertUser, k -> {
            int seq = nextSeqByConcert.computeIfAbsent(concertId,
                    concert -> new AtomicInteger(1)).getAndIncrement();

            if (seq > MAX_QUEUE_SIZE) {
                throw new HighConcurrencyTicketingException(ErrorCode.CONFLICT, "대기열 최대 인원을 초과했습니다.");
            }

            userIdByConcertAndSeq.put(new ConcertSeq(concertId, seq), userId);
            waitingSeqByConcert.computeIfAbsent(concertId, concert -> new WaitingSeqTree(MAX_QUEUE_SIZE)).add(seq, 1);

            return seq;
        });

        return getStatus(concertId, userId);
    }

    @Override
    public ReservationQueueStatusResponse getStatus(Long concertId, Long userId) {
        Integer seq = seqByConcertAndUser.get(new ConcertUser(concertId, userId));
        if (seq == null) throw new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 사용자가 대기열에 없습니다.");

        int lastPermittedSeq = lastPermittedSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(MAX_PERMITTED_SIZE)).get();

        if (seq <= lastPermittedSeq) {
            return new ReservationQueueStatusResponse(concertId, userId, ReservationQueueStatusType.PERMITTED, 0);
        }

        WaitingSeqTree waitingSeqTree = waitingSeqByConcert.get(concertId);
        return new ReservationQueueStatusResponse(concertId, userId, ReservationQueueStatusType.WAITING,
                waitingSeqTree.getRank(seq) - waitingSeqTree.getRank(lastPermittedSeq));
    }

    @Override
    public boolean isPermitted(Long concertId, Long userId) {
        Integer seq = seqByConcertAndUser.get(new ConcertUser(concertId, userId));
        int permittedSeq = lastPermittedSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(MAX_PERMITTED_SIZE)).get();
        return seq != null && seq <= permittedSeq;
    }


    @Override
    public boolean leaveAndWasPermitted(Long concertId, Long userId) {
        Integer seq = seqByConcertAndUser.remove(new ConcertUser(concertId, userId));
        if (seq == null) throw new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 사용자가 대기열에 없습니다.");

        userIdByConcertAndSeq.remove(new ConcertSeq(concertId, seq));
        waitingSeqByConcert.get(concertId).add(seq, -1);
        return seq <= lastPermittedSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(MAX_PERMITTED_SIZE)).get();
    }

    @Override
    public Optional<Long> permitNextWaitingUser(Long concertId) {
        int nextSeq = nextSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(1)).get();

        AtomicInteger lastPermittedSeq = lastPermittedSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(MAX_PERMITTED_SIZE));

        while (nextSeq > lastPermittedSeq.get()) {
            int nextPermittedSeq = lastPermittedSeq.incrementAndGet();
            Long userId = userIdByConcertAndSeq.get(new ConcertSeq(concertId, nextPermittedSeq));
            if (userId != null) {
                return Optional.of(userId);
            }
        }
        return Optional.empty();
    }

    private record ConcertUser(Long concertId, Long userId) {
    }

    private record ConcertSeq(Long concertId, int seq) {

    }
}
