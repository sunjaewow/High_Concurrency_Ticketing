package com.highconcurrency.ticketing.presentation;

import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation-queues/{concertId}/users/{userId}")
@RequiredArgsConstructor
@Tag(name = "Reservation Queue")
public class ReservationQueueController {

    private final ReservationQueueUseCase reservationQueueUseCase;

    @PostMapping
    @Operation(summary = "예약 대기열 진입")
    public ResponseEntity<ReservationQueueStatusResponse> enterReservationQueue(
            @Parameter(description = "콘서트 ID") @PathVariable Long concertId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationQueueUseCase.enterQueue(concertId, userId));
    }

    @GetMapping
    @Operation(summary = "예약 대기열 상태 조회")
    public ResponseEntity<ReservationQueueStatusResponse> getReservationQueueStatus(
            @Parameter(description = "콘서트 ID") @PathVariable Long concertId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        return ResponseEntity.ok(reservationQueueUseCase.getQueueStatus(concertId, userId));
    }

    @DeleteMapping
    @Operation(summary = "예약 대기열 이탈")
    public ResponseEntity<Void> leaveReservationQueue(
            @Parameter(description = "콘서트 ID") @PathVariable Long concertId,
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        reservationQueueUseCase.leaveQueue(concertId, userId);
        return ResponseEntity.noContent().build();
    }
}
