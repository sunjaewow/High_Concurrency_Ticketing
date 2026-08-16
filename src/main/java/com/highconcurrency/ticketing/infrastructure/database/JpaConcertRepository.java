package com.highconcurrency.ticketing.infrastructure.database;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertRepository extends JpaRepository<Concert, Long>, ConcertRepository {
}
