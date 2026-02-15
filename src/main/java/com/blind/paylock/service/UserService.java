package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.ChangeUserRoleRequestDto;
import com.blind.paylock.datalayer.dto.request.UpdateUserRequestDto;
import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserProfileResponseDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;


public interface UserService {

    Mono<ApiResponse<UserResponseDto>> registerUser(
        UserCreateRequestDto request,  String requestRefId
    );

    Mono<ApiResponse<UserProfileResponseDto>> getUserProfile(  String requestRefId);


    // change user role
    Mono<ApiResponse<UserResponseDto>> changeUserRole(String userId,
                                                      ChangeUserRoleRequestDto request);

    // disable user
    Mono<ApiResponse<UserResponseDto>> changeUserStatus(String userId,String status);

    // edit user details
    Mono<ApiResponse<UserResponseDto>> updateUserDetails(String userId,
                                                       UpdateUserRequestDto request);
}
