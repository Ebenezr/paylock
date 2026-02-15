package com.blind.paylock.service;

import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.component.UserComponent;
import com.blind.paylock.service.impl.UserServiceImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplMoreTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserComponent userComponent;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, walletRepository, userComponent, null);
    }

    @Test
    void getUserProfile_whenAuthenticated_shouldReturnProfile() {
        UUID userId = UUID.randomUUID();
        User u = User.builder().id(userId).name("N").email("e@x.com").status(null).createdAt(LocalDateTime.now()).password("p").role(null).build();
        when(userRepository.findById(userId)).thenReturn(Mono.just(u));

        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        ApiResponse<?> r = userService.getUserProfile(ResponseFactory.newRequestRefId()).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)).block();
        assertThat(r).isNotNull();
    }

    @Test
    void changeUserRole_whenUserNotFound_shouldThrow() {
        String id = UUID.randomUUID().toString();
        when(userRepository.findById(java.util.UUID.fromString(id))).thenReturn(Mono.empty());

        assertThatThrownBy(() -> userService.changeUserRole(id, null).block())
                .isInstanceOf(com.blind.paylock.exception.UserNotFoundException.class);
    }
}
