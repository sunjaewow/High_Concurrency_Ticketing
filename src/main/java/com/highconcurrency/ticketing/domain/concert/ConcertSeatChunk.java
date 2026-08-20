package com.highconcurrency.ticketing.domain.concert;

import com.highconcurrency.ticketing.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert_seat_chunks",
        uniqueConstraints = @UniqueConstraint(name = "uk_concert_seat_chunks_concert_id_chunk_no", columnNames = {"concert_id", "chunk_no"}))
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConcertSeatChunk extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(nullable = false)
    private int chunkNo;

    @Column(nullable = false)
    private int remainingSeatCount;

    public static ConcertSeatChunk create(Concert concert, int chunkNo, int chunkSize) {
        return ConcertSeatChunk.builder()
                .concert(concert)
                .chunkNo(chunkNo)
                .remainingSeatCount(chunkSize)
                .build();
    }

    public boolean reserve() {
        if (remainingSeatCount <= 0) {
            return false;
        }
        remainingSeatCount--;
        return true;
    }
}
