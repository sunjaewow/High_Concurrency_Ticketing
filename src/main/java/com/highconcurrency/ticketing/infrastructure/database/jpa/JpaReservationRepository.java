package com.highconcurrency.ticketing.infrastructure.database.jpa;

import com.highconcurrency.ticketing.domain.reservation.Reservation;
import com.highconcurrency.ticketing.domain.reservation.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepository {
}
