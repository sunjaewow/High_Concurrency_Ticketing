package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.domain.concert.Concert;

import java.util.List;

public interface ConcertUseCase {

    Long createConcert(ConcertCreateRequest request);

    Concert getConcert(Long concertId);

    List<ConcertResponse> getConcertList();

    void delete(Long concertId);

    ConcertResponse getConcertResponse(Long concert);
}
