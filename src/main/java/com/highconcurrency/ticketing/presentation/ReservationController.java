package com.highconcurrency.ticketing.presentation;

import com.highconcurrency.ticketing.application.usecase.reservation.ReservationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reservation")
public class ReservationController {

    private final ReservationUseCase reservationUseCase;

    @PostMapping("/{concertId}/users/{userId}")
    @Operation(summary = "콘서트 예약")
    public ResponseEntity<Long> reserveTicket(
            @Parameter(description = "콘서트 ID") @PathVariable Long concertId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationUseCase.createReservation(concertId, userId));
    }
}
