package com.blind.paylock.datalayer.dto.request;

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
}
