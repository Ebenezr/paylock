package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponseDto {

    private String userId;
    private String name;
    private String email;
}
