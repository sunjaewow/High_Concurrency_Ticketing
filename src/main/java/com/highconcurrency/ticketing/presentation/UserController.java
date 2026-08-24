package com.highconcurrency.ticketing.presentation;

import com.highconcurrency.ticketing.application.usecase.user.UserCreateRequest;
import com.highconcurrency.ticketing.application.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User")
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    @Operation(summary = "사용자 생성")
    public ResponseEntity<Long> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userUseCase.createUser(request));
    }
}
