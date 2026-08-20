package com.highconcurrency.ticketing.domain.concert;

import com.highconcurrency.ticketing.application.usecase.concert.ConcertResponse;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {
    Concert save(Concert concert);

    Optional<Concert> findById(Long concertId);

    Optional<ConcertResponse> findByIdWithRemainingSeatCount(Long concertId);

    List<ConcertResponse> findAllWithRemainingSeatCount();

    List<Concert> findAll();

    void deleteById(Long concertId);
}
