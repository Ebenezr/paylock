package com.blind.paylock.service;

import com.blind.paylock.component.UserComponent;
import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.datalayer.model.Wallet;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.impl.UserServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.UserRoles;
import com.blind.paylock.utils.enums.UserStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserComponent userComponent;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, walletRepository, userComponent, passwordEncoder);
    }

    @Test
    void registerUser_whenEmailNotExists_shouldSaveAndReturn() {
        UserCreateRequestDto req = new UserCreateRequestDto();
        req.setEmail("new@example.com");
        req.setPassword("p");

        User built = User.builder()
                .id(UUID.randomUUID())
                .name("New")
                .email("new@example.com")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .password("raw")
                .role(UserRoles.USER)
                .build();

        when(userRepository.findByEmail(eq("new@example.com"))).thenReturn(Mono.empty());
        when(userComponent.buildNewUser(eq(req))).thenReturn(built);
        when(passwordEncoder.encode(eq("p"))).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(Mono.just(built));
        when(walletRepository.save(any())).thenReturn(Mono.just(new Wallet(built.getId())));

        ApiResponse<UserResponseDto> r = userService.registerUser(req, ResponseFactory.newRequestRefId()).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getEmail()).isEqualTo("new@example.com");
    }
}

