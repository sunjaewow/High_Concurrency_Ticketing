package com.highconcurrency.ticketing.application.usecase.reservation;

public interface ReservationUseCase {
    Long createReservation(Long concertId, Long userId);
}
