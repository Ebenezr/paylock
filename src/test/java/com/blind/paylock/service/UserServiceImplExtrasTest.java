package com.blind.paylock.service;

import com.blind.paylock.component.UserComponent;
import com.blind.paylock.datalayer.dto.request.UpdateUserRequestDto;
import com.blind.paylock.datalayer.dto.response.UserResponseDto;
import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.repository.UserRepository;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.impl.UserServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.enums.UserRoles;
import com.blind.paylock.utils.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplExtrasTest {

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
    void updateUserDetails_shouldSaveAndReturn() {
        UUID id = UUID.randomUUID();
        UpdateUserRequestDto req = new UpdateUserRequestDto();
        req.setName("NewName");
        User u = User.builder().id(id).name("Old").email("e@x.com").status(UserStatus.ACTIVE).createdAt(LocalDateTime.now()).password("p").role(UserRoles.USER).build();
        when(userRepository.findById(id)).thenReturn(Mono.just(u));
        when(userRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        ApiResponse<UserResponseDto> r = userService.updateUserDetails(id.toString(), req).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody().getName()).isEqualTo("NewName");
    }

    @Test
    void listUsers_shouldReturnMappedList() {
        User u1 = User.builder().id(UUID.randomUUID()).name("A").email("a@x.com").status(UserStatus.ACTIVE).createdAt(LocalDateTime.now()).password("p").role(UserRoles.USER).build();
        when(userRepository.findAll()).thenReturn(Flux.just(u1));

        ApiResponse<List<UserResponseDto>> r = userService.listUsers().block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).hasSize(1);
    }
}
