package com.highconcurrency.ticketing.application.usecase.reservationqueue;

public interface ReservationQueueUseCase {

    ReservationQueueStatusResponse enterQueue(Long concertId, Long userId);

    ReservationQueueStatusResponse getQueueStatus(Long concertId, Long userId);

    void leaveQueue(Long concertId, Long userId);

    void validatePermitted(Long concertId, Long userId);
}
