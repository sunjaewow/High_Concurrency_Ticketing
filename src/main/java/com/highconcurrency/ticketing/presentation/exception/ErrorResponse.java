package com.highconcurrency.ticketing.presentation.exception;

import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;

public record ErrorResponse(
        String code,
        String message
) {

    public static ErrorResponse from(HighConcurrencyTicketingException exception) {
        return new ErrorResponse(exception.getErrorCode().name(), exception.getMessage());
    }
}
