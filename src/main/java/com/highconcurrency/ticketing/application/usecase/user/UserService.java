package com.highconcurrency.ticketing.application.usecase.user;

import com.highconcurrency.ticketing.application.common.ErrorCode;
import com.highconcurrency.ticketing.application.common.HighConcurrencyTicketingException;
import com.highconcurrency.ticketing.domain.user.User;
import com.highconcurrency.ticketing.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new HighConcurrencyTicketingException(ErrorCode.NOT_FOUND, "해당 사용자가 없습니다."));
    }
}
