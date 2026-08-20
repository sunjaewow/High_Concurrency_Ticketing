package com.highconcurrency.ticketing.infrastructure.database.redis;

import com.highconcurrency.ticketing.application.out.ConcertSeatChunkPort;
import com.highconcurrency.ticketing.domain.concert.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisConcertSeatChunkAdapter implements ConcertSeatChunkPort {

    private final static String KEY_PREFIX = "concert:seat:chunk:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public int getChunkNo(Concert concert, int chunkCount) {
        String key = KEY_PREFIX + concert.getId();
        Long nextNum = redisTemplate.opsForValue().increment(key);
        return (nextNum.intValue() - 1) % chunkCount;
    }
}
