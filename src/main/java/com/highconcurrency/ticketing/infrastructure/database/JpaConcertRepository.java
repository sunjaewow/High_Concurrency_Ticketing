package com.highconcurrency.ticketing.infrastructure.database;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaConcertRepository extends JpaRepository<Concert, Long>, ConcertRepository {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Concert c where c.id = :concertId")
    Optional<Concert> findByIdForUpdate(@Param("concertId") Long concertId);
}
