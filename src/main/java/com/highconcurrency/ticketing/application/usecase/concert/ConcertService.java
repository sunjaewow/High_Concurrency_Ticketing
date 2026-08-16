package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService implements ConcertUseCase {
    private final ConcertRepository concertRepository;

    @Override
    @Transactional
    public Long createConcert(ConcertCreateRequest request) {
        Concert savedConcert = concertRepository.save(Concert.create(request.title(), request.totalSeatCount()));
        return savedConcert.getId();
    }

    @Override
    public ConcertResponse getConcert(Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(() -> new IllegalArgumentException("Concert not found with id: " + concertId));
        return ConcertResponse.create(concert);
    }

    @Override
    public List<ConcertResponse> getConcertList() {
        List<Concert> list = concertRepository.findAll();
        return list.stream().map(ConcertResponse::create).toList();
    }

    @Override
    public void delete(Long concertId) {
        concertRepository.deleteById(concertId);
    }
}
