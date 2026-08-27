package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertRepository;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunkCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService implements ConcertUseCase {
    private final ConcertRepository concertRepository;
    private final ConcertSeatChunkUseCase concertSeatChunkUseCase;
    private final ConcertSeatChunkCounterRepository concertSeatChunkCounterRepository;

    @Override
    @Transactional
    public Long createConcert(ConcertCreateRequest request) {
        Concert savedConcert = concertRepository.save(Concert.create(request.title(), request.totalSeatCount()));
        concertSeatChunkUseCase.initializeSeatChunks(savedConcert);
        return savedConcert.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Concert getConcert(Long concertId) {
        return concertRepository.findById(concertId)
                .orElseThrow(() -> new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 콘서트가 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public ConcertResponse getConcertResponse(Long concertId) {
        return concertRepository.findByIdWithRemainingSeatCount(concertId)
                .orElseThrow(() -> new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 콘서트가 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConcertResponse> getConcertList() {
        return concertRepository.findAllWithRemainingSeatCount();
    }

    @Override
    @Transactional
    public void delete(Long concertId) {
        Concert concert = getConcert(concertId);
        concertRepository.delete(concert);
        concertSeatChunkUseCase.deleteByConcert(concert);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                concertSeatChunkCounterRepository.deleteChunkNo(concert);
            }
        });
    }
}
