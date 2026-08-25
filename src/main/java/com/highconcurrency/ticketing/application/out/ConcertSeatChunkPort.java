package com.highconcurrency.ticketing.application.out;

import com.highconcurrency.ticketing.domain.concert.Concert;

public interface ConcertSeatChunkPort {
    int getChunkNo(Concert concert, int chunkCount);

    void deleteChunkNo(Concert concert);
}
