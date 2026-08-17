package com.highconcurrency.ticketing.presentation;

import com.highconcurrency.ticketing.application.usecase.reservation.ReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationUseCase reservationUseCase;

    @PostMapping("/{concertId}/users/{userId}")
    public ResponseEntity<Long> reserveTicket(@PathVariable Long concertId, @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationUseCase.createReservation(concertId, userId));
    }
}
