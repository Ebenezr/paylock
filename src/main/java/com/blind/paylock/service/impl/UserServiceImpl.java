package com.blind.paylock.service.impl;

import com.blind.paylock.component.UserComponent;
import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserProfileResponseDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.datalayer.model.Wallet;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.UserService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.UserRoles;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final UserComponent userComponent;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<ApiResponse<UserResponseDto>> registerUser(
            UserCreateRequestDto request,
            String requestRefId
    ) {
        return userRepository.findByEmail(request.getEmail())
                .flatMap(existing ->
                        ResponseFactory.<UserResponseDto>errorMono(
                                HttpStatus.CONFLICT,
                                "USER_EXISTS",
                                "User with this email already exists",
                                requestRefId
                        )
                )
                .switchIfEmpty(Mono.defer(() -> {

                    User newUser = userComponent.buildNewUser(request);
                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));

                    Wallet wallet = new Wallet(newUser.getId());

                    return userRepository.save(newUser)
                            .flatMap(saved ->
                                    walletRepository.save(wallet).thenReturn(saved)
                            )
                            .flatMap(saved ->
                                    ResponseFactory.success(
                                            UserResponseDto.builder()
                                                    .userId(saved.getId().toString())
                                                    .name(saved.getName())
                                                    .email(saved.getEmail())
                                                    .status(saved.getStatus())
                                                    .role(saved.getRole())
                                                    .build(),
                                            requestRefId
                                    )
                            );
                }));
    }

    @Override
    public Mono<ApiResponse<UserProfileResponseDto>> getUserProfile(
            String requestRefId
    ) {
        return ReactiveSecurityUtil.currentUserId()
                .flatMap(userId ->
                        userRepository.findById(userId)
                                .flatMap(user ->
                                        ResponseFactory.success(
                                                UserProfileResponseDto.builder()
                                                        .userId(user.getId().toString())
                                                        .name(user.getName())
                                                        .email(user.getEmail())
                                                        .status(user.getStatus())
                                                        .role(UserRoles.USER)
                                                        .createdAt(user.getCreatedAt())
                                                        .build(),
                                                requestRefId
                                        )
                                )
                )
                .switchIfEmpty(
                        ResponseFactory.errorMono(
                                HttpStatus.UNAUTHORIZED,
                                "UNAUTHORIZED",
                                "User not authenticated",
                                requestRefId
                        )
                );
    }

}
