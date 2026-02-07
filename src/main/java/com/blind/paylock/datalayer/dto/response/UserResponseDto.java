package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private String userId;
    private String name;
    private String email;
    private String status;
}
