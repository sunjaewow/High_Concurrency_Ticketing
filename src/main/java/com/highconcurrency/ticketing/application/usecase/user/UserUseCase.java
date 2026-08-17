package com.highconcurrency.ticketing.application.usecase.user;

import com.highconcurrency.ticketing.domain.user.User;

public interface UserUseCase {
    Long createUser(UserCreateRequest request);

    User getUser(Long userId);
}
