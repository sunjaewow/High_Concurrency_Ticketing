package com.highconcurrency.ticketing.application.usecase.concert;

import java.util.List;

public interface ConcertUseCase {

    Long createConcert(ConcertCreateRequest request);

    ConcertResponse getConcert(Long concertId);

    List<ConcertResponse> getConcertList();

    void delete(Long concertId);
}
