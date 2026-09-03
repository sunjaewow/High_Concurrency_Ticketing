package com.highconcurrency.ticketing.domain.concert;

public interface ConcertSeatChunkCounterRepository {
    int getChunkNo(Concert concert, int chunkCount);

    void deleteByConcert(Concert concert);
}
