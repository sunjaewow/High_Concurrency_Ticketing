package com.highconcurrency.ticketing.presentation.exception;

import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HighConcurrencyTicketingException.class)
    public ResponseEntity<ErrorResponse> handleHighConcurrencyTicketingException(HighConcurrencyTicketingException exception) {
        return ResponseEntity
                .status(exception.getErrorCode().getStatus())
                .body(ErrorResponse.from(exception));
    }
}
