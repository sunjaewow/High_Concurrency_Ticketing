package com.highconcurrency.ticketing.application.usecase.reservationqueue;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ReservationQueueUseCase {

    ReservationQueueStatusResponse enterQueue(Long concertId, Long userId);

    ReservationQueueStatusResponse getQueueStatus(Long concertId, Long userId);

    void leaveQueue(Long concertId, Long userId);

    void validatePermitted(Long concertId, Long userId);

    SseEmitter subscribe(Long concertId, Long userId);
}
