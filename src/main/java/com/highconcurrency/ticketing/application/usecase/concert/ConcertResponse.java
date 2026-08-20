package com.highconcurrency.ticketing.application.usecase.concert;

public record ConcertResponse(
        Long id,
        String title,
        int totalSeatCount,
        Long remainingSeatCount
) {
}
