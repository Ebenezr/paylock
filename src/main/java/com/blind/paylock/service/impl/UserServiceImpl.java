package com.blind.paylock.service.impl;

import com.blind.paylock.component.UserComponent;
import com.blind.paylock.datalayer.dto.request.ChangeUserRoleRequestDto;
import com.blind.paylock.datalayer.dto.request.UpdateUserRequestDto;
import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserProfileResponseDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.datalayer.model.Wallet;
import com.blind.paylock.exception.UserNotFoundException;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.UserService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.UserRoles;
import com.blind.paylock.utils.enums.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

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
                                                    .userId(String.valueOf(saved.getId()))
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
                                                        .userId(String.valueOf(user.getId()))
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


    @Override
    public Mono<ApiResponse<UserResponseDto>> changeUserRole(
            String userId,
            ChangeUserRoleRequestDto request
    ) {
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    user.setRole(UserRoles.valueOf(request.getRole()));
                    return userRepository.save(user);
                })
                .flatMap(savedUser -> ResponseFactory.success(
                        UserResponseDto.builder()
                                .userId(String.valueOf(savedUser.getId()))
                                .name(savedUser.getName())
                                .email(savedUser.getEmail())
                                .status(savedUser.getStatus())
                                .role(savedUser.getRole())
                                .build(),
                        "Role updated successfully"
                ));
    }

    @Override
    public Mono<ApiResponse<UserResponseDto>> changeUserStatus(
            String userId,
            String status
    ) {
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    user.setStatus(UserStatus.valueOf(status));
                    return userRepository.save(user);
                })
                .flatMap(savedUser -> ResponseFactory.success(
                        UserResponseDto.builder()
                                .userId(String.valueOf(savedUser.getId()))
                                .name(savedUser.getName())
                                .email(savedUser.getEmail())
                                .status(savedUser.getStatus())
                                .role(savedUser.getRole())
                                .build(),
                        "User disabled successfully"
                ));
    }

    @Override
    public Mono<ApiResponse<UserResponseDto>> updateUserDetails(
            String userId,
            UpdateUserRequestDto request
    ) {
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {

                    if (request.getName() != null)
                        user.setName(request.getName());

                    if (request.getEmail() != null)
                        user.setEmail(request.getEmail());

                    return userRepository.save(user);
                })
                .flatMap(savedUser -> ResponseFactory.success(
                        UserResponseDto.builder()
                                .userId(String.valueOf(savedUser.getId()))
                                .name(savedUser.getName())
                                .email(savedUser.getEmail())
                                .status(savedUser.getStatus())
                                .role(savedUser.getRole())
                                .build(),
                        "User updated successfully"
                ));
    }

    @Override
    public Mono<ApiResponse<List<UserResponseDto>>> listUsers() {
        return userRepository.findAll()
                .map(user -> UserResponseDto.builder()
                        .userId(String.valueOf(user.getId()))
                        .name(user.getName())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .role(user.getRole())
                        .build()
                )
                .collectList()
                .flatMap(list -> ResponseFactory.success(list, ResponseFactory.newRequestRefId()));
    }


}
