package com.blind.paylock.service;

import com.blind.paylock.component.JwtUtil;
import com.blind.paylock.datalayer.dto.request.LoginRequestDto;
import com.blind.paylock.datalayer.dto.response.LoginResponseDto;
import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.service.impl.AuthServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.UserStatus;
import com.blind.paylock.utils.enums.UserRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        LoginRequestDto req = new LoginRequestDto();
        req.setEmail("a@b.com");
        req.setPassword("secret");

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("User")
                .email("a@b.com")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .password("encoded")
                .role(UserRoles.USER)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("tok-123");

        ApiResponse<LoginResponseDto> r = authService.login(req, ResponseFactory.newRequestRefId()).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getAccessToken()).isEqualTo("tok-123");
    }
}

