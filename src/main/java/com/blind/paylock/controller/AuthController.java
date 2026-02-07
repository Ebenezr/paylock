package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.LoginRequestDto;
import com.blind.paylock.datalayer.dto.response.LoginResponseDto;
import com.blind.paylock.service.AuthService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ApiResponse<LoginResponseDto>> login(
            @RequestBody LoginRequestDto request
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();
        return authService.login(request, requestRefId);
    }
}
