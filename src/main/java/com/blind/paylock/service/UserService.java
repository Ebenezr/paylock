package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserProfileResponseDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;


public interface UserService {

    Mono<ApiResponse<UserResponseDto>> registerUser(
        UserCreateRequestDto request
    );

    Mono<ApiResponse<UserProfileResponseDto>> getUserProfile();
}
