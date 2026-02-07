package com.blind.paylock.component;

import com.blind.paylock.datalayer.dto.request.UserCreateRequestDto;
import com.blind.paylock.datalayer.model.User;
import com.blind.paylock.utils.enums.UserRoles;
import com.blind.paylock.utils.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserComponentImpl implements UserComponent {

    @Override
    public User buildNewUser(UserCreateRequestDto request) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .status(UserStatus.ACTIVE)
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
