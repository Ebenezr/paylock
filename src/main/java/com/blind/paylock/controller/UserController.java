package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.ChangeUserRoleRequestDto;
import com.blind.paylock.datalayer.dto.request.UpdateUserRequestDto;
import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserProfileResponseDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.service.UserService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @GetMapping
    public Mono<ApiResponse<List<UserResponseDto>>> listUsers() {
        return userService.listUsers();
    }

    @PatchMapping("/{userId}/role")
    public Mono<ApiResponse<UserResponseDto>> changeUserRole(
            @PathVariable String userId,
            @Valid @RequestBody ChangeUserRoleRequestDto request
    ) {
        // Prevent user from changing their own role
        return ReactiveSecurityUtil.currentUserIdAsString()
                .flatMap(currentId -> {
                    if (currentId.equals(userId)) {
                        return ResponseFactory.<UserResponseDto>errorMono(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_ACTION",
                                "Users cannot change their own role",
                                ResponseFactory.newRequestRefId()
                        );
                    }
                    return userService.changeUserRole(userId, request);
                })
                .switchIfEmpty(
                        // If not authenticated, let the service/auth layer handle it
                        ResponseFactory.<UserResponseDto>errorMono(
                                HttpStatus.UNAUTHORIZED,
                                "UNAUTHORIZED",
                                "User not authenticated",
                                ResponseFactory.newRequestRefId()
                        )
                );
    }

    @PatchMapping("/{userId}/status")
    public Mono<ApiResponse<UserResponseDto>> changeUserStatus(
            @PathVariable String userId,
            @RequestParam String status
    ) {
        // Prevent user from disabling/changing their own status
        return ReactiveSecurityUtil.currentUserIdAsString()
                .flatMap(currentId -> {
                    if (currentId.equals(userId)) {
                        return ResponseFactory.<UserResponseDto>errorMono(
                                HttpStatus.BAD_REQUEST,
                                "INVALID_ACTION",
                                "Users cannot change their own status",
                                ResponseFactory.newRequestRefId()
                        );
                    }
                    return userService.changeUserStatus(userId, status);
                })
                .switchIfEmpty(
                        ResponseFactory.<UserResponseDto>errorMono(
                                HttpStatus.UNAUTHORIZED,
                                "UNAUTHORIZED",
                                "User not authenticated",
                                ResponseFactory.newRequestRefId()
                        )
                );
    }

    @PutMapping("/{userId}")
    public Mono<ApiResponse<UserResponseDto>> updateUserDetails(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequestDto request
    ) {
        return userService.updateUserDetails(userId, request);
    }
}
