package com.highconcurrency.ticketing.domain.reservation;

import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;

import java.util.Optional;

public interface ReservationQueueRepository {

    ReservationQueueStatusResponse enter(Long concertId, Long userId);

    ReservationQueueStatusResponse getStatus(Long concertId, Long userId);

    boolean isPermitted(Long concertId, Long userId);
    
    Optional<Long> permitNextWaitingUser(Long concertId);

    boolean leaveAndWasPermitted(Long concertId, Long userId);
}
