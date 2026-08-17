package com.highconcurrency.ticketing.application.usecase.reservation;

import com.highconcurrency.ticketing.application.usecase.concert.ConcertUseCase;
import com.highconcurrency.ticketing.application.usecase.user.UserUseCase;
import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.reservation.Reservation;
import com.highconcurrency.ticketing.domain.reservation.ReservationRepository;
import com.highconcurrency.ticketing.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService implements ReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final ConcertUseCase concertUseCase;
    private final UserUseCase userUseCase;

    @Override
    @Transactional
    public Long createReservation(Long concertId, Long userId) {
        User user = userUseCase.getUser(userId);
        Concert concert = concertUseCase.getConcert(concertId);
        concert.reserve();
        return reservationRepository.save(Reservation.create(user, concert)).getId();
    }
}
