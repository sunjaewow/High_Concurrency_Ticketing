package com.highconcurrency.ticketing.application.usecase.reservationqueue;

import lombok.Builder;

@Builder
public record ReservationQueueStatusResponse(
        Long concertId,
        Long userId,
        ReservationQueueStatusType status,
        int seq
) {
}
