package com.highconcurrency.ticketing.test;

import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusResponse;
import com.highconcurrency.ticketing.application.usecase.reservationqueue.ReservationQueueStatusType;
import com.highconcurrency.ticketing.infrastructure.database.java.JavaReservationQueueRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaReservationQueueRepositoryTest {

    private final JavaReservationQueueRepository reservationQueueRepository = new JavaReservationQueueRepository();

    @Test
    void 같은_사용자가_같은_콘서트_대기열에_다시_진입하면_기존_상태를_반환한다() {
        ReservationQueueStatusResponse firstResponse = reservationQueueRepository.enter(1L, 1L);
        ReservationQueueStatusResponse secondResponse = reservationQueueRepository.enter(1L, 1L);

        assertThat(secondResponse).isEqualTo(firstResponse);
    }

    @Test
    void 허용_인원_안에_있는_사용자는_예약_가능_상태가_된다() {
        ReservationQueueStatusResponse response = reservationQueueRepository.enter(1L, 1L);

        assertThat(response.status()).isEqualTo(ReservationQueueStatusType.PERMITTED);
        assertThat(response.seq()).isZero();
    }

    @Test
    void 허용_인원을_초과한_사용자는_대기_상태와_대기_순번을_받는다() {
        for (long userId = 1L; userId <= 10000L; userId++) {
            reservationQueueRepository.enter(1L, userId);
        }

        ReservationQueueStatusResponse response = reservationQueueRepository.enter(1L, 10001L);

        assertThat(response.status()).isEqualTo(ReservationQueueStatusType.WAITING);
        assertThat(response.seq()).isEqualTo(1);
    }

    @Test
    void 예약_가능_사용자가_대기열을_떠나면_다음_대기_사용자가_예약_가능_상태가_된다() {
        for (long userId = 1L; userId <= 10001L; userId++) {
            reservationQueueRepository.enter(1L, userId);
        }

        boolean wasPermitted = reservationQueueRepository.leaveAndWasPermitted(1L, 1L);
        assertThat(wasPermitted).isTrue();

        assertThat(reservationQueueRepository.permitNextWaitingUser(1L)).contains(10001L);

        ReservationQueueStatusResponse response = reservationQueueRepository.getStatus(1L, 10001L);

        assertThat(response.status()).isEqualTo(ReservationQueueStatusType.PERMITTED);
        assertThat(response.seq()).isZero();
    }

    @Test
    void 대기_중인_사용자가_대기열을_떠나면_다음_대기_사용자의_순번이_앞당겨진다() {
        for (long userId = 1L; userId <= 10002L; userId++) {
            reservationQueueRepository.enter(1L, userId);
        }

        boolean wasPermitted = reservationQueueRepository.leaveAndWasPermitted(1L, 10001L);
        assertThat(wasPermitted).isFalse();

        ReservationQueueStatusResponse response = reservationQueueRepository.getStatus(1L, 10002L);

        assertThat(response.status()).isEqualTo(ReservationQueueStatusType.WAITING);
        assertThat(response.seq()).isEqualTo(1);
    }

    @Test
    void 여러_대기_사용자가_이탈하면_남은_사용자의_순번이_이탈한_수만큼_앞당겨진다() {
        for (long userId = 1L; userId <= 10005L; userId++) {
            reservationQueueRepository.enter(1L, userId);
        }

        reservationQueueRepository.leaveAndWasPermitted(1L, 10001L);
        reservationQueueRepository.leaveAndWasPermitted(1L, 10003L);

        ReservationQueueStatusResponse response = reservationQueueRepository.getStatus(1L, 10005L);

        assertThat(response.status()).isEqualTo(ReservationQueueStatusType.WAITING);
        assertThat(response.seq()).isEqualTo(3);
    }

    @Test
    void 대기열에_없는_사용자_상태를_조회하면_예외가_발생한다() {
        assertThatThrownBy(() -> reservationQueueRepository.getStatus(1L, 1L))
                .isInstanceOf(HighConcurrencyTicketingException.class);
    }
}
