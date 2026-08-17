package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.domain.concert.Concert;

public record ConcertResponse(
        String title,
        int totalSeatCount,
        int reservedSeatCount
) {
    public static ConcertResponse from(Concert concert) {
        return new ConcertResponse(
                concert.getTitle(),
                concert.getTotalSeatCount(),
                concert.getReservedCount()
        );
    }
}
