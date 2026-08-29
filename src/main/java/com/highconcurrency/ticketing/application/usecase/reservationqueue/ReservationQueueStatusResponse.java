package com.highconcurrency.ticketing.application.usecase.reservationqueue;

public record ReservationQueueStatusResponse(
        Long concertId,
        Long userId,
        ReservationQueueStatus status,
        int seq
) {
}
