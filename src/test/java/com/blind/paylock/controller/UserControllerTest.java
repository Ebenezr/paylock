package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.ChangeUserRoleRequestDto;
import com.blind.paylock.datalayer.dto.request.UpdateUserRequestDto;
import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserProfileResponseDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.service.UserService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    void register_shouldDelegateToService() {
        UserCreateRequestDto req = new UserCreateRequestDto();
        req.setEmail("a@b.com");

        UserResponseDto resp = UserResponseDto.builder()
                .userId("id-1")
                .email("a@b.com")
                .build();

        when(userService.registerUser(eq(req), any())).thenReturn(ResponseFactory.success(resp, "ref-1"));

        ApiResponse<UserResponseDto> r = userController.register(req).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void profile_shouldCallService() {
        UserProfileResponseDto profile = UserProfileResponseDto.builder()
                .userId("id-1")
                .email("me@example.com")
                .build();

        when(userService.getUserProfile(any())).thenReturn(ResponseFactory.success(profile, "ref-2"));

        ApiResponse<UserProfileResponseDto> r = userController.profile().block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getEmail()).isEqualTo("me@example.com");
    }

    @Test
    void listUsers_shouldDelegate() {
        when(userService.listUsers()).thenReturn(ResponseFactory.success(List.of(), "ref-3"));

        ApiResponse<List<UserResponseDto>> r = userController.listUsers().block();
        assertThat(r).isNotNull();
    }

    @Test
    void updateUserDetails_shouldCallService() {
        UpdateUserRequestDto req = new UpdateUserRequestDto();
        when(userService.updateUserDetails(eq("u1"), eq(req))).thenReturn(ResponseFactory.success(UserResponseDto.builder().userId("u1").build(), "ref"));

        ApiResponse<UserResponseDto> r = userController.updateUserDetails("u1", req).block();
        assertThat(r).isNotNull();
    }



    @Test
    void changeUserRole_whenDifferent_shouldDelegateToService() {
        ChangeUserRoleRequestDto req = new ChangeUserRoleRequestDto();
        req.setRole("ADMIN");

        String principalId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        Authentication auth = new UsernamePasswordAuthenticationToken(principalId, "x", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(userService.changeUserRole(eq(targetId), eq(req))).thenReturn(ResponseFactory.success(UserResponseDto.builder().userId(targetId).build(), "ref"));

        ApiResponse<UserResponseDto> r = userController.changeUserRole(targetId, req)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                .block();

        assertThat(r).isNotNull();
        assertThat(r.getHeader().getResponseCode()).isEqualTo(200);
    }

    @Test
    void changeUserStatus_whenDifferent_shouldDelegate() {
        String principalId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        Authentication auth = new UsernamePasswordAuthenticationToken(principalId, "x", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(userService.changeUserStatus(eq(targetId), eq("DISABLED"))).thenReturn(ResponseFactory.success(UserResponseDto.builder().userId(targetId).build(), "ref"));

        ApiResponse<UserResponseDto> r = userController.changeUserStatus(targetId, "DISABLED")
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                .block();
        assertThat(r).isNotNull();
        assertThat(r.getHeader().getResponseCode()).isEqualTo(200);
    }
}
