package com.highconcurrency.ticketing.application.usecase.user;

public interface UserUseCase {
    Long createUser(UserCreateRequest request);
}
