package com.highconcurrency.ticketing.domain.reservation;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
}
