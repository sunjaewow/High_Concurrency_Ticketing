package com.highconcurrency.ticketing.infrastructure.database.java;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatus;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;
import com.highconcurrency.ticketing.domain.reservation.ReservationQueueRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class JavaReservationQueueRepository implements ReservationQueueRepository {

    private static final int MAX_PERMITTED_SIZE = 10000;

    private final ConcurrentHashMap<Long, AtomicInteger> nextSeqByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> lastPermittedSeqByConcert = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ConcertUser, Integer> seqByConcertAndUser = new ConcurrentHashMap<>();

    @Override
    public ReservationQueueStatusResponse enter(Long concertId, Long userId) {
        seqByConcertAndUser.computeIfAbsent(new ConcertUser(concertId, userId),
                concertUser -> nextSeqByConcert.computeIfAbsent(concertId,
                        concert -> new AtomicInteger(1)).getAndIncrement());

        return getStatus(concertId, userId);
    }

    @Override
    public ReservationQueueStatusResponse getStatus(Long concertId, Long userId) {
        Integer seq = seqByConcertAndUser.get(new ConcertUser(concertId, userId));
        if (seq == null) throw new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 사용자가 대기열에 없습니다.");

        int lastPermittedSeq = lastPermittedSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(MAX_PERMITTED_SIZE)).get();

        if (seq <= lastPermittedSeq) {
            return new ReservationQueueStatusResponse(concertId, userId, ReservationQueueStatus.PERMITTED, 0);
        }

        return new ReservationQueueStatusResponse(concertId, userId, ReservationQueueStatus.WAITING, seq - lastPermittedSeq);
    }

    @Override
    public boolean isPermitted(Long concertId, Long userId) {
        Integer seq = seqByConcertAndUser.get(new ConcertUser(concertId, userId));
        return seq != null && isPermitted(concertId, seq);
    }


    @Override
    public void leave(Long concertId, Long userId) {
        Integer seq = seqByConcertAndUser.remove(new ConcertUser(concertId, userId));
        if (seq == null) throw new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 사용자가 대기열에 없습니다.");

        if (isPermitted(concertId, seq))
            lastPermittedSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(MAX_PERMITTED_SIZE)).incrementAndGet();
    }

    private boolean isPermitted(Long concertId, int seq) {
        int permittedSeq = lastPermittedSeqByConcert.computeIfAbsent(concertId, k -> new AtomicInteger(MAX_PERMITTED_SIZE)).get();
        return seq <= permittedSeq;
    }

    private record ConcertUser(Long concertId, Long userId) {

    }
}
