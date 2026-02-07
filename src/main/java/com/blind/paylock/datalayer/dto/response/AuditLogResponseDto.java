package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponseDto {

    private String entityId;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
}
