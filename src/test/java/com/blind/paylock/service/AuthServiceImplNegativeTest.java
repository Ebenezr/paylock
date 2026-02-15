package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.LoginRequestDto;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.service.impl.AuthServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplNegativeTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, null);
    }

    @Test
    void login_withUnknownEmail_shouldReturnUnauthorized() {
        LoginRequestDto req = new LoginRequestDto();
        req.setEmail("noone@example.com");
        req.setPassword("x");

        when(userRepository.findByEmail("noone@example.com")).thenReturn(Mono.empty());

        ApiResponse<?> r = authService.login(req, ResponseFactory.newRequestRefId()).block();
        assertThat(r).isNotNull();
        assertThat(r.getHeader().getResponseCode()).isEqualTo(401);
    }
}

