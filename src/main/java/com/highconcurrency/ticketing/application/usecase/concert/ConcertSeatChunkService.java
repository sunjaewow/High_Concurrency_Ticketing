package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunk;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertSeatChunkService implements ConcertSeatChunkUseCase {

    private final static int CHUNK_SIZE = 1000;

    private final ConcertSeatChunkRepository concertSeatChunkRepository;

    @Override
    @Transactional
    public ConcertSeatChunk getConcertSeatChunkLocked(Concert concert, int chunkNo) {
        return concertSeatChunkRepository.findByIdForUpdate(concert, chunkNo)
                .orElseThrow(() -> new IllegalArgumentException("Concert seat chunk not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public int getChunkCount(Concert concert) {
        return concertSeatChunkRepository.countByConcert(concert);
    }

    @Override
    @Transactional
    public void initializeSeatChunks(Concert concert) {
        int chunkCount = concert.getTotalSeatCount() / CHUNK_SIZE;

        List<ConcertSeatChunk> concertSeatChunkList = new ArrayList<>();
        for (int chunkNo = 0; chunkNo < chunkCount; chunkNo++) {
            concertSeatChunkList.add(ConcertSeatChunk.create(concert, chunkNo, CHUNK_SIZE));
        }

        concertSeatChunkRepository.saveChunks(concertSeatChunkList);
    }
}
