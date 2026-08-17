package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public Concert getConcert(Long concertId) {
        return concertRepository.findById(concertId).orElseThrow(() -> new IllegalArgumentException("Concert not found with id: " + concertId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConcertResponse> getConcertList() {
        List<Concert> list = concertRepository.findAll();
        return list.stream().map(ConcertResponse::from).toList();
    }

    @Override
    @Transactional
    public void delete(Long concertId) {
        concertRepository.deleteById(concertId);
    }
}
