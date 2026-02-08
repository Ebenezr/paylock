package com.blind.paylock.datalayer.dto.response;

import com.blind.paylock.utils.enums.UserRoles;
import com.blind.paylock.utils.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {

    private String userId;
    private String name;
    private String email;
    private UserStatus status;
    private LocalDateTime createdAt;
    private UserRoles role;
}
