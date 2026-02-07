package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.LoginRequestDto;
import com.blind.paylock.datalayer.dto.response.LoginResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<ApiResponse<LoginResponseDto>> login(
        LoginRequestDto request,
        String requestRefId
    );
}
