package com.highconcurrency.ticketing.application.usecase.reservation;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.application.usecase.concert.ConcertSeatChunkUseCase;
import com.highconcurrency.ticketing.application.usecase.concert.ConcertUseCase;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueUseCase;
import com.highconcurrency.ticketing.application.usecase.user.UserUseCase;
import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunk;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunkCounterRepository;
import com.highconcurrency.ticketing.domain.reservation.Reservation;
import com.highconcurrency.ticketing.domain.reservation.ReservationRepository;
import com.highconcurrency.ticketing.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ReservationService implements ReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final ConcertUseCase concertUseCase;
    private final UserUseCase userUseCase;
    private final ConcertSeatChunkUseCase concertSeatChunkUseCase;
    private final ConcertSeatChunkCounterRepository concertSeatChunkCounterRepository;
    private final ReservationQueueUseCase reservationQueueUseCase;

    @Override
    @Transactional
    public Long createReservation(Long concertId, Long userId) {
        reservationQueueUseCase.validatePermitted(concertId, userId);

        User user = userUseCase.getUser(userId);
        Concert concert = concertUseCase.getConcert(concertId);
        int chunkCount = concertSeatChunkUseCase.getChunkCount(concert);
        int startChunkNo = concertSeatChunkCounterRepository.getChunkNo(concert, chunkCount);

        for (int i = 0; i < chunkCount; i++) {
            int chunkNo = (startChunkNo + i) % chunkCount;
            ConcertSeatChunk concertSeatChunkLocked = concertSeatChunkUseCase.getConcertSeatChunkLocked(concert, chunkNo);
            if (concertSeatChunkLocked.reserve()) {
                Long reservationId = reservationRepository.save(Reservation.create(user, concert, concertSeatChunkLocked)).getId();
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        reservationQueueUseCase.leaveQueue(concertId, userId);
                    }
                });
                return reservationId;
            }
        }
        throw new HighConcurrencyTicketingException(ErrorCode.CONFLICT, "예약 가능한 좌석이 없습니다.");
    }
}
