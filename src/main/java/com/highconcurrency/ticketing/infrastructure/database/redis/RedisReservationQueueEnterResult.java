package com.highconcurrency.ticketing.infrastructure.database.redis;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;

import java.util.List;

record RedisReservationQueueEnterResult(
        long statusCode,
        long waitingRank
) {

    private static final long FULL = -1L;
    private static final long PERMITTED = 1L;

    static RedisReservationQueueEnterResult from(List<?> result) {
        return new RedisReservationQueueEnterResult(
                toLong(result.get(0)),
                toLong(result.get(1))
        );
    }

    private static long toLong(Object value) {
        return Long.parseLong(String.valueOf(value));
    }

    void validateQueueNotFull() {
        if (statusCode == FULL) {
            throw new HighConcurrencyTicketingException(ErrorCode.CONFLICT, "대기열 최대 인원을 초과했습니다.");
        }
    }

    boolean isPermitted() {
        return statusCode == PERMITTED;
    }

    int getWaitingRank() {
        return (int) waitingRank;
    }
}
