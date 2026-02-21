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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Mono<ApiResponse<List<UserResponseDto>>> listUsers() {
        return userService.listUsers();
    }

    @PreAuthorize("hasRole('ADMIN') and #userId != authentication.name")
    @PatchMapping("/{userId}/role")
    public Mono<ApiResponse<UserResponseDto>> changeUserRole(
            @PathVariable String userId,
            @Valid @RequestBody ChangeUserRoleRequestDto request
    ) {
        return userService.changeUserRole(userId, request);
    }

    @PreAuthorize("hasRole('ADMIN') and #userId != authentication.name")
    @PatchMapping("/{userId}/status")
    public Mono<ApiResponse<UserResponseDto>> changeUserStatus(
            @PathVariable String userId,
            @RequestParam String status
    ) {
        return userService.changeUserStatus(userId, status);
    }

    @PutMapping("/{userId}")
    public Mono<ApiResponse<UserResponseDto>> updateUserDetails(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequestDto request
    ) {
        return userService.updateUserDetails(userId, request);
    }
}
