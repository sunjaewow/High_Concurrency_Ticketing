package com.highconcurrency.ticketing.application.usecase.reservationqueue;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.domain.reservation.ReservationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationQueueService implements ReservationQueueUseCase {

    private final ReservationQueueRepository reservationQueueRepository;

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
        reservationQueueRepository.leave(concertId, userId);
    }

    @Override
    public void validatePermitted(Long concertId, Long userId) {
        if (!reservationQueueRepository.isPermitted(concertId, userId)) {
            throw new HighConcurrencyTicketingException(ErrorCode.FORBIDDEN, "예약이 허용된 사용자가 아닙니다.");
        }
    }
}
