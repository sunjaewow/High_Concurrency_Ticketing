package com.highconcurrency.ticketing.application.usecase.reservationqueue;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.application.port.EventPublisherPort;
import com.highconcurrency.ticketing.domain.reservation.ReservationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class ReservationQueueService implements ReservationQueueUseCase {

    private final ReservationQueueRepository reservationQueueRepository;
    private final EventPublisherPort eventPublisherPort;

    @Override
    public ReservationQueueStatusResponse enterQueue(Long concertId, Long userId) {
        return reservationQueueRepository.enter(concertId, userId);
    }

    @Override
    public ReservationQueueStatusResponse getQueueStatus(Long concertId, Long userId) {
        return reservationQueueRepository.getStatus(concertId, userId);
    }

    @Override
    public void leaveQueue(Long concertId, Long userId) {
        boolean wasPermitted = reservationQueueRepository.leaveAndWasPermitted(concertId, userId);

        eventPublisherPort.close(concertId, userId);

        if (wasPermitted) reservationQueueRepository.permitNextWaitingUser(concertId).ifPresent(nextUserId -> {
            eventPublisherPort.publish(concertId, nextUserId,
                    ReservationQueueStatusResponse.builder()
                            .concertId(concertId)
                            .userId(nextUserId)
                            .status(ReservationQueueStatusType.PERMITTED)
                            .seq(0)
                            .build());
            eventPublisherPort.close(concertId, nextUserId);
        });
    }

    @Override
    public void validatePermitted(Long concertId, Long userId) {
        if (!reservationQueueRepository.isPermitted(concertId, userId)) {
            throw new HighConcurrencyTicketingException(ErrorCode.FORBIDDEN, "예약이 허용된 사용자가 아닙니다.");
        }
    }

    @Override
    public SseEmitter subscribe(Long concertId, Long userId) {
        return eventPublisherPort.subscribe(concertId, userId);
    }
}
