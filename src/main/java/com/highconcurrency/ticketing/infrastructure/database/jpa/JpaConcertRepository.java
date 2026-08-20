package com.highconcurrency.ticketing.infrastructure.database.jpa;

import com.highconcurrency.ticketing.application.usecase.concert.ConcertResponse;
import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaConcertRepository extends JpaRepository<Concert, Long>, ConcertRepository {
    @Override
    @Query("""
            select new com.highconcurrency.ticketing.application.usecase.concert.ConcertResponse(
                c.id, c.title, c.totalSeatCount, sum(cs.remainingSeatCount)
                )
                from Concert c left join ConcertSeatChunk cs on c = cs.concert
                where c.id = :concertId group by c.id, c.title, c.totalSeatCount
            """)
    Optional<ConcertResponse> findByIdWithRemainingSeatCount(@Param("concertId") Long concertId);

    @Override
    @Query("""
            select new com.highconcurrency.ticketing.application.usecase.concert.ConcertResponse(
                c.id, c.title, c.totalSeatCount, sum(cs.remainingSeatCount)
                )
                from Concert c left join ConcertSeatChunk cs on c = cs.concert
                group by c.id, c.title, c.totalSeatCount
            """)
    List<ConcertResponse> findAllWithRemainingSeatCount();
}
