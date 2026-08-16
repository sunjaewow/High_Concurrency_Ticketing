package com.highconcurrency.ticketing.presentation;

import com.highconcurrency.ticketing.application.usecase.user.UserCreateRequest;
import com.highconcurrency.ticketing.application.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userUseCase.createUser(request));
    }

}
