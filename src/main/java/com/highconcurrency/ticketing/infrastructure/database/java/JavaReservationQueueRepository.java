package com.highconcurrency.ticketing.infrastructure.database.java;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusType;
import com.highconcurrency.ticketing.domain.reservation.ReservationQueueRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class JavaReservationQueueRepository implements ReservationQueueRepository {

    private static final int MAX_PERMITTED_SIZE = 10000;
    private static final int MAX_QUEUE_SIZE = 100000;

    private final ConcurrentHashMap<Long, AtomicInteger> nextSeqByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> lastPermittedSeqByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, int[]> prefixSumByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ConcertUser, Integer> seqByConcertAndUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ConcertSeq, Long> userIdByConcertAndSeq = new ConcurrentHashMap<>();

    @Override
    public ReservationQueueStatusResponse enter(Long concertId, Long userId) {
        ConcertUser concertUser = new ConcertUser(concertId, userId);

        seqByConcertAndUser.computeIfAbsent(concertUser, k -> {
            int seq = nextSeqByConcert.computeIfAbsent(concertId,
                    concert -> new AtomicInteger(1)).getAndIncrement();
            validateQueueSize(seq);

            userIdByConcertAndSeq.put(new ConcertSeq(concertId, seq), userId);
            addPrefixSum(concertId, seq);

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

        return new ReservationQueueStatusResponse(concertId, userId, ReservationQueueStatusType.WAITING,
                getRank(concertId, seq) - getRank(concertId, lastPermittedSeq));
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
        removePrefixSum(concertId, seq);
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

    private void validateQueueSize(int seq) {
        if (seq > MAX_QUEUE_SIZE) {
            throw new HighConcurrencyTicketingException(ErrorCode.CONFLICT, "대기열 최대 인원을 초과했습니다.");
        }
    }

    private void addPrefixSum(Long concertId, int seq) {
        int[] prefixSum = prefixSumByConcert.computeIfAbsent(concertId, concert -> new int[MAX_QUEUE_SIZE + 1]);
        synchronized (prefixSum) {
            prefixSum[seq] = prefixSum[seq - 1] + 1;
        }
    }

    private void removePrefixSum(Long concertId, int seq) {
        int[] prefixSum = prefixSumByConcert.get(concertId);
        int lastSeq = Math.min(nextSeqByConcert.get(concertId).get() - 1, MAX_QUEUE_SIZE);

        synchronized (prefixSum) {
            for (int index = seq; index <= lastSeq; index++) {
                prefixSum[index]--;
            }
        }
    }

    private int getRank(Long concertId, int seq) {
        int[] prefixSum = prefixSumByConcert.get(concertId);
        if (seq <= 0) return 0;
        if (seq > MAX_QUEUE_SIZE) return prefixSum[MAX_QUEUE_SIZE];

        synchronized (prefixSum) {
            return prefixSum[seq];
        }
    }
}
