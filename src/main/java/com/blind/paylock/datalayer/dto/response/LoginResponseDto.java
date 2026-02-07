package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String accessToken;
    private String userId;
    private String role;
}
