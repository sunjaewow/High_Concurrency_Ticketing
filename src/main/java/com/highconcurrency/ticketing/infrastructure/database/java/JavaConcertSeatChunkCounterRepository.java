package com.highconcurrency.ticketing.infrastructure.database.java;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunkCounterRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Profile("java")
public class JavaConcertSeatChunkCounterRepository implements ConcertSeatChunkCounterRepository {

    private final ConcurrentHashMap<Long, AtomicInteger> chunkNoByConcertId = new ConcurrentHashMap<>();

    @Override
    public int getChunkNo(Concert concert, int chunkCount) {
        AtomicInteger counter = chunkNoByConcertId.computeIfAbsent(concert.getId(), key -> new AtomicInteger(0));
        return counter.getAndIncrement() % chunkCount;
    }

    @Override
    public void deleteChunkNo(Concert concert) {
        chunkNoByConcertId.remove(concert.getId());
    }
}
