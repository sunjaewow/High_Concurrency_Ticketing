package com.highconcurrency.ticketing.application.usecase.user;

public record UserCreateRequest(
        String email,
        String name
) {
}
