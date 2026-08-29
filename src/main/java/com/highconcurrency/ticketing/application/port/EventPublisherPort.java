package com.highconcurrency.ticketing.application.port;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EventPublisherPort {
    SseEmitter subscribe(Long concertId, Long userId);

    void publish(Long concertId, Long userId, Object event);

    void close(Long concertId, Long userId);
}
