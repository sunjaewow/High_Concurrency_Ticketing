package com.highconcurrency.ticketing.application.usecase.user;

import com.highconcurrency.ticketing.domain.user.User;
import com.highconcurrency.ticketing.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long createUser(UserCreateRequest userCreateRequest) {
        User user = User.create(userCreateRequest.email(), userCreateRequest.name());

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }
}
