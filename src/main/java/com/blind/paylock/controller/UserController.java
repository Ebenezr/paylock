package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserProfileResponseDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.service.UserService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<ApiResponse<UserResponseDto>> register(
            @Valid @RequestBody UserCreateRequestDto request
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();
        return userService.registerUser(request, requestRefId);
    }

    @GetMapping("/me")
    public Mono<ApiResponse<UserProfileResponseDto>> profile() {
        String requestRefId = ResponseFactory.newRequestRefId();
        return userService.getUserProfile(requestRefId);
    }
}
