package com.highconcurrency.ticketing.infrastructure.database.redis;

import com.highconcurrency.ticketing.domain.concert.Concert;
import com.highconcurrency.ticketing.domain.concert.ConcertSeatChunkCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("redis")
@RequiredArgsConstructor
public class RedisConcertSeatChunkCounterRepository implements ConcertSeatChunkCounterRepository {

    private final static String KEY_PREFIX = "concert:seat:chunk:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public int getChunkNo(Concert concert, int chunkCount) {
        String key = KEY_PREFIX + concert.getId();
        Long nextNum = redisTemplate.opsForValue().increment(key);
        return (nextNum.intValue() - 1) % chunkCount;
    }

    @Override
    public void deleteChunkNo(Concert concert) {
        redisTemplate.delete(KEY_PREFIX + concert.getId());
    }
}
