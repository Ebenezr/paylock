package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.response.AuditLogResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {

    Mono<ApiResponse<List<AuditLogResponseDto>>> getAuditLogs(
        LocalDateTime from,
        LocalDateTime to,
        int page,
        int size
    );
}
