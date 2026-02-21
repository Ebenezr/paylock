package com.blind.paylock.service.impl;

import com.blind.paylock.component.JwtUtil;
import com.blind.paylock.component.PaylockLogManager;
import com.blind.paylock.datalayer.dto.request.LoginRequestDto;
import com.blind.paylock.datalayer.dto.response.LoginResponseDto;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.service.AuthService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<ApiResponse<LoginResponseDto>> login(
            LoginRequestDto request,
            String requestRefId
    ) {
        long startTime = System.currentTimeMillis();

        PaylockLogManager.info(requestRefId,
                "AUTH_LOGIN_ENTRY",
                PaylockLogManager.processDuration(startTime),
                "REQUEST_RECEIVED");


        return userRepository.findByEmail(request.getEmail())
            .flatMap(user -> {
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                    PaylockLogManager.error(
                            requestRefId,
                            "AUTH_LOGIN",
                            PaylockLogManager.processDuration(startTime),
                            "INVALID_PASSWORD"
                    );

                    return ResponseFactory.<LoginResponseDto>errorMono(
                        HttpStatus.UNAUTHORIZED,
                        "INVALID_CREDENTIALS",
                        "Invalid email or password",
                        requestRefId
                    );
                }

                if (UserStatus.ACTIVE != user.getStatus()) {

                    PaylockLogManager.error(
                            requestRefId,
                            "AUTH_LOGIN",
                            PaylockLogManager.processDuration(startTime),
                            "USER_DISABLED"
                    );

                    return ResponseFactory.<LoginResponseDto>errorMono(
                        HttpStatus.FORBIDDEN,
                        "USER_DISABLED",
                        "User account is disabled",
                        requestRefId
                    );
                }

                String token = jwtUtil.generateToken(user);

                PaylockLogManager.info(
                        requestRefId,
                        "AUTH_LOGIN",
                        PaylockLogManager.processDuration(startTime),
                        "LOGIN_SUCCESS"
                );

                return ResponseFactory.success(
                    LoginResponseDto.builder()
                        .accessToken(token)
                        .userId(user.getId().toString())
                        .role(user.getRole().name())
                        .build(),
                    requestRefId
                );
            })
                .switchIfEmpty(Mono.defer(() -> {
                    PaylockLogManager.error(
                            requestRefId,
                            "AUTH_LOGIN",
                            PaylockLogManager.processDuration(startTime),
                            "USER_NOT_FOUND"
                    );
                    return ResponseFactory.errorMono(
                            HttpStatus.UNAUTHORIZED,
                            "INVALID_CREDENTIALS",
                            "Invalid email or password",
                            requestRefId
                    );
                }
            )).doOnError(error ->
                                PaylockLogManager.error(
                                        requestRefId,
                                        "AUTH_LOGIN",
                                        PaylockLogManager.processDuration(startTime),
                                        "SYSTEM_ERROR: " + error.getMessage()
                                )
                );

    }
}
