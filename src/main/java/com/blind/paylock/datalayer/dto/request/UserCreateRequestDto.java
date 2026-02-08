package com.blind.paylock.datalayer.dto.request;

import com.blind.paylock.utils.enums.UserRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequestDto {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private UserRoles role;
}
