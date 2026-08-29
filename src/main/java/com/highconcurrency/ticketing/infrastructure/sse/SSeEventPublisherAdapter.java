package com.highconcurrency.ticketing.infrastructure.sse;

import com.highconcurrency.ticketing.application.port.EventPublisherPort;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SSeEventPublisherAdapter implements EventPublisherPort {

    private static final Long TIMEOUT = 60 * 1000L;
    private final ConcurrentHashMap<ConcertUser, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long concertId, Long userId) {
        ConcertUser concertUser = new ConcertUser(concertId, userId);
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT);

        emitters.put(concertUser, sseEmitter);

        sseEmitter.onCompletion(() -> emitters.remove(concertUser));
        sseEmitter.onTimeout(() -> emitters.remove(concertUser));
        sseEmitter.onError(error -> emitters.remove(concertUser));

        return sseEmitter;
    }

    @Override
    public void publish(Long concertId, Long userId, Object object) {
        SseEmitter sseEmitter = emitters.get(new ConcertUser(concertId, userId));
        if (sseEmitter == null) return;

        try {
            sseEmitter.send(object);
        } catch (Exception e) {
            emitters.remove(new ConcertUser(concertId, userId));
        }
    }

    @Override
    public void close(Long concertId, Long userId) {
        SseEmitter sseEmitter = emitters.remove(new ConcertUser(concertId, userId));
        if (sseEmitter != null) {
            sseEmitter.complete();
        }
    }


    private record ConcertUser(Long concertId, Long userId) {
    }
}
