package com.highconcurrency.ticketing.application.usecase.concert;

public record ConcertCreateRequest(String title, int totalSeatCount) {
}
