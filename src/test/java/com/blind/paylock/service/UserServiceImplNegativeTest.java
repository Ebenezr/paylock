package com.blind.paylock.service;

import com.blind.paylock.component.UserComponent;
import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.impl.UserServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplNegativeTest {

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
    void registerUser_whenEmailExists_shouldReturnConflict() {
        UserCreateRequestDto req = new UserCreateRequestDto();
        req.setEmail("exists@example.com");
        User existing = User.builder().id(UUID.randomUUID()).name("Ex").email("exists@example.com").status(null).createdAt(LocalDateTime.now()).password("p").role(null).build();

        when(userRepository.findByEmail("exists@example.com")).thenReturn(Mono.just(existing));

        ApiResponse<?> r = userService.registerUser(req, ResponseFactory.newRequestRefId()).block();
        assertThat(r).isNotNull();
        assertThat(r.getHeader().getResponseCode()).isEqualTo(409);
    }
}

