package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.domain.concert.Concert;

import java.util.List;

public interface ConcertUseCase {

    Long createConcert(ConcertCreateRequest request);

    List<ConcertResponse> getConcertList();

    void delete(Long concertId);

    Concert getConcert(Long concertId);

}
