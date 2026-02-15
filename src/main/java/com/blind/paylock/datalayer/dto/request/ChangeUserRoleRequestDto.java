package com.blind.paylock.datalayer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChangeUserRoleRequestDto {

    @NotBlank
    private String role;
}
