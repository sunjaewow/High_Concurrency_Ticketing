package com.highconcurrency.ticketing.infrastructure.database.jpa;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunk;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunkRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaConcertSeatChunkRepository extends JpaRepository<ConcertSeatChunk, Long>, ConcertSeatChunkRepository {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ConcertSeatChunk c where c.concert = :concert and c.chunkNo = :chunkNo")
    Optional<ConcertSeatChunk> findByIdForUpdate(@Param("concert") Concert concert, @Param("chunkNo") int chunkNo);

    @Override
    int countByConcert(Concert concert);

    @Override
    default void saveChunks(List<ConcertSeatChunk> concertSeatChunkList) {
        saveAll(concertSeatChunkList);
    }

    @Override
    @Modifying
    @Query("delete from ConcertSeatChunk c where c.concert = :concert")
    void deleteByConcert(@Param("concert") Concert concert);
}
