package com.highconcurrency.ticketing.domain.reservation;

import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;

public interface ReservationQueueRepository {

    ReservationQueueStatusResponse enter(Long concertId, Long userId);

    ReservationQueueStatusResponse getStatus(Long concertId, Long userId);

    boolean isPermitted(Long concertId, Long userId);

    void leave(Long concertId, Long userId);
}
