package com.highconcurrency.ticketing.domain.reservation;

import com.highconcurrency.ticketing.domain.BaseEntity;
import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunk;
import com.highconcurrency.ticketing.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations", uniqueConstraints = @UniqueConstraint(name = "uk_reservations_user_id_concert_id", columnNames = {"user_id", "concert_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @ManyToOne
    @JoinColumn(name = "concert_seat_chunk_id", nullable = false)
    private ConcertSeatChunk concertSeatChunk;

    public static Reservation create(User user, Concert concert, ConcertSeatChunk concertSeatChunk) {
        return Reservation.builder()
                .user(user)
                .concert(concert)
                .concertSeatChunk(concertSeatChunk)
                .build();
    }
}
