package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.LoginRequestDto;
import com.blind.paylock.datalayer.dto.response.LoginResponseDto;
import com.blind.paylock.service.AuthService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    void login_shouldReturnSuccessResponse() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("secret");

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken("token-123")
                .userId("user-1")
                .role("USER")
                .build();

        when(authService.login(eq(request), anyString()))
                .thenReturn(ResponseFactory.success(loginResponseDto, "ref-1"));

        Mono<ApiResponse<LoginResponseDto>> resultMono = authController.login(request);
        ApiResponse<LoginResponseDto> response = resultMono.block();

        assertThat(response).isNotNull();
        assertThat(response.getHeader()).isNotNull();
        assertThat(response.getHeader().getResponseCode()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("token-123");
        assertThat(response.getBody().getUserId()).isEqualTo("user-1");
        assertThat(response.getBody().getRole()).isEqualTo("USER");
    }
}

