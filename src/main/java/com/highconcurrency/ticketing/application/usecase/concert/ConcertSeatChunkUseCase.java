package com.highconcurrency.ticketing.application.usecase.concert;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunk;

public interface ConcertSeatChunkUseCase {
    ConcertSeatChunk getConcertSeatChunkLocked(Concert concert, int chunkNo);

    int getChunkCount(Concert concert);

    void initializeSeatChunks(Concert concert);
}
