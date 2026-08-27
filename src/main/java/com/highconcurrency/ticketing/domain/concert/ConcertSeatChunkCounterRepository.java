package com.highconcurrency.ticketing.domain.concert;

public interface ConcertSeatChunkCounterRepository {
    int getChunkNo(Concert concert, int chunkCount);

    void deleteChunkNo(Concert concert);
}
