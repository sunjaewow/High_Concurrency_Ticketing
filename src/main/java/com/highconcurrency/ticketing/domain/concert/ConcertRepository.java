package com.highconcurrency.ticketing.domain.concert;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {
    Concert save(Concert concert);

    Optional<Concert> findById(Long concertId);

    Optional<Concert> findByIdForUpdate(Long concertId);

    List<Concert> findAll();

    void deleteById(Long concertId);
}
