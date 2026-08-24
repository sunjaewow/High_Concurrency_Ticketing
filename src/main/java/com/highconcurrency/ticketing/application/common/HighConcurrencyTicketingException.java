package com.highconcurrency.ticketing.application.common;

import lombok.Getter;

@Getter
public class HighConcurrencyTicketingException extends RuntimeException {

    private final ErrorCode errorCode;

    public HighConcurrencyTicketingException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
