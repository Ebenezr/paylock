package com.blind.paylock.service.impl;

import com.blind.paylock.component.PaylockLogManager;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final UserComponent userComponent;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, WalletRepository walletRepository, UserComponent userComponent, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.userComponent = userComponent;
        this.passwordEncoder = passwordEncoder;
    }

    LocalDateTime startTime = LocalDateTime.now();


    @Override
    public Mono<ApiResponse<UserResponseDto>> registerUser(
            UserCreateRequestDto request,
            String requestRefId


    ) {
        PaylockLogManager.info(requestRefId,
        "ENTRY_USER_REGISTRATION",
                PaylockLogManager.processDuration(startTime),
                "REQUEST_RECEIVED"
            );

        return userRepository.findByEmail(request.getEmail())
                .flatMap(existing ->{

                        PaylockLogManager.error(
                                requestRefId,
                                "USER_REGISTRATION_ERROR",
                                PaylockLogManager.processDuration(startTime),
                                "USER_REGISTRATION_RESPONSE_USER_EXISTS"
                        );

                        return ResponseFactory.<UserResponseDto>errorMono(
                                HttpStatus.CONFLICT,
                                "USER_EXISTS",
                                "User with this email already exists",
                                requestRefId
                        );
                }
                )
                .switchIfEmpty(Mono.defer(() -> {

                    User newUser = userComponent.buildNewUser(request);
                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));

                    Wallet wallet = new Wallet(newUser.getId());

                    return userRepository.save(newUser)
                            .flatMap(saved ->
                                    walletRepository.save(wallet).thenReturn(saved)
                            )
                            .flatMap(saved ->{

                                PaylockLogManager.info(
                                        requestRefId,
                                        "USER_REGISTRATION_SUCCESS",
                                        PaylockLogManager.processDuration(startTime),
                                        "USER_REGISTRATION_RESPONSE_SUCCESS"
                                );
                                    return ResponseFactory.success(
                                            UserResponseDto.builder()
                                                    .userId(String.valueOf(saved.getId()))
                                                    .name(saved.getName())
                                                    .email(saved.getEmail())
                                                    .status(saved.getStatus())
                                                    .role(saved.getRole())
                                                    .build(),
                                            requestRefId
                                    );
                            }
                            );
                }));
    }

    @Override
    public Mono<ApiResponse<UserProfileResponseDto>> getUserProfile(
            String requestRefId
    ) {

        PaylockLogManager.info(
                requestRefId,
                "ENTRY_GET_USER_PROFILE",
                PaylockLogManager.processDuration(startTime),
                "REQUEST_RECEIVED"
        );

        return ReactiveSecurityUtil.currentUserId()
                .flatMap(userId ->
                        userRepository.findById(userId)
                                .flatMap(user ->{
                                        PaylockLogManager.info(
                                                requestRefId,
                                                "GET_USER_PROFILE_SUCCESS",
                                                PaylockLogManager.processDuration(startTime),
                                                "USER_PROFILE_RESPONSE_SUCCESS"
                                        );
                                        return ResponseFactory.success(
                                                UserProfileResponseDto.builder()
                                                        .userId(String.valueOf(user.getId()))
                                                        .name(user.getName())
                                                        .email(user.getEmail())
                                                        .status(user.getStatus())
                                                        .role(UserRoles.USER)
                                                        .createdAt(user.getCreatedAt())
                                                        .build(),
                                                requestRefId
                                        );
    }
                                )
                )
                .switchIfEmpty(Mono.defer(() -> {
                            PaylockLogManager.error(
                                    requestRefId,
                                    "GET_USER_PROFILE_ERROR",
                                    PaylockLogManager.processDuration(startTime),
                                    "USER_PROFILE_RESPONSE_UNAUTHORIZED"
                            );
                            return ResponseFactory.errorMono(
                                    HttpStatus.UNAUTHORIZED,
                                    "UNAUTHORIZED",
                                    "User not authenticated",
                                    requestRefId
                            );
                        })
                );
    }


    @Override
    public Mono<ApiResponse<UserResponseDto>> changeUserRole(
            String userId,
            ChangeUserRoleRequestDto request
    ) {
        PaylockLogManager.info(
                userId,
                "ENTRY_CHANGE_USER_ROLE",
                PaylockLogManager.processDuration(startTime),
                "REQUEST_RECEIVED"
        );
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    user.setRole(UserRoles.valueOf(request.getRole()));
                    return userRepository.save(user);
                })
                .flatMap(savedUser -> {
                        PaylockLogManager.info(
                                userId,
                                "CHANGE_USER_ROLE_SUCCESS",
                                PaylockLogManager.processDuration(startTime),
                                "CHANGE_USER_ROLE_RESPONSE_SUCCESS"
                        );
                        return ResponseFactory.success(

                         UserResponseDto.builder()
                                .userId(String.valueOf(savedUser.getId()))
                                .name(savedUser.getName())
                                .email(savedUser.getEmail())
                                .status(savedUser.getStatus())
                                .role(savedUser.getRole())
                                .build(),
                        "Role updated successfully"
                );
                }
                );
    }

    @Override
    public Mono<ApiResponse<UserResponseDto>> changeUserStatus(
            String userId,
            String status
    ) {
        PaylockLogManager.info(
                userId,
                "ENTRY_CHANGE_USER_STATUS",
                PaylockLogManager.processDuration(startTime),
                "REQUEST_RECEIVED"
        );

        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    user.setStatus(UserStatus.valueOf(status));
                    PaylockLogManager.info(
                            userId,
                            "USER_STATUS_UPDATED",
                            PaylockLogManager.processDuration(startTime),
                            "USER_STATUS_UPDATED_TO_" + status
                    );
                    return userRepository.save(user);
                })
                .flatMap(savedUser -> {
                    PaylockLogManager.info(
                            userId,
                            "CHANGE_USER_STATUS_SUCCESS",
                            PaylockLogManager.processDuration(startTime),
                            "CHANGE_USER_STATUS_RESPONSE_SUCCESS"
                    );

                    return ResponseFactory.success(
                            UserResponseDto.builder()
                                    .userId(String.valueOf(savedUser.getId()))
                                    .name(savedUser.getName())
                                    .email(savedUser.getEmail())
                                    .status(savedUser.getStatus())
                                    .role(savedUser.getRole())
                                    .build(),
                            "User disabled successfully"
                    );
                });
    }

    @Override
    public Mono<ApiResponse<UserResponseDto>> updateUserDetails(
            String userId,
            UpdateUserRequestDto request
    ) {

        PaylockLogManager.info(
                userId,
                "ENTRY_UPDATE_USER_DETAILS",
                PaylockLogManager.processDuration(startTime),
                "REQUEST_RECEIVED"
        );

        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {

                    if (request.getName() != null)
                        user.setName(request.getName());

                    if (request.getEmail() != null)
                        user.setEmail(request.getEmail());

                    return userRepository.save(user);
                })
                .flatMap(savedUser -> {

                    PaylockLogManager.info(
                            userId,
                            "UPDATE_USER_DETAILS_SUCCESS",
                            PaylockLogManager.processDuration(startTime),
                            "UPDATE_USER_DETAILS_RESPONSE_SUCCESS"
                    );

                    return ResponseFactory.success(
                            UserResponseDto.builder()
                                    .userId(String.valueOf(savedUser.getId()))
                                    .name(savedUser.getName())
                                    .email(savedUser.getEmail())
                                    .status(savedUser.getStatus())
                                    .role(savedUser.getRole())
                                    .build(),
                            "User updated successfully"
                    );
                });
    }

    @Override
    public Mono<ApiResponse<List<UserResponseDto>>> listUsers() {

        PaylockLogManager.info(
                "LIST_USERS",
                "ENTRY_LIST_USERS",
                PaylockLogManager.processDuration(startTime),
                "REQUEST_RECEIVED"
        );

        return userRepository.findAll()
                .map(user -> {

                        PaylockLogManager.info(
                                String.valueOf(user.getId()),
                                "USER_LISTED",
                                PaylockLogManager.processDuration(startTime),
                                "USER_LISTED_SUCCESSFULLY"
                        );

                    return UserResponseDto.builder()
                                    .userId(String.valueOf(user.getId()))
                                    .name(user.getName())
                                    .email(user.getEmail())
                                    .status(user.getStatus())
                                    .role(user.getRole())
                                    .build();
                        }
                )
                .collectList()
                .flatMap(list -> {
                    PaylockLogManager.info(
                            "LIST_USERS",
                            "LIST_USERS_SUCCESS",
                            PaylockLogManager.processDuration(startTime),
                            "LIST_USERS_RESPONSE_SUCCESS"
                    );
                     return
                    ResponseFactory.success(list, ResponseFactory.newRequestRefId());
                });
    }


}
