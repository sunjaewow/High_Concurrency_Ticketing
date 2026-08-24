package com.highconcurrency.ticketing.domain.concert;

import java.util.List;
import java.util.Optional;

public interface ConcertSeatChunkRepository {
    Optional<ConcertSeatChunk> findByIdForUpdate(Concert concert, int chunkNo);

    int countByConcert(Concert concert);

    void saveChunks(List<ConcertSeatChunk> concertSeatChunkList);
}
