package com.blind.paylock.service;

import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

public interface AuditService {

    Mono<ApiResponse> getAuditLogs(
        Map<String, String> headers,
        LocalDateTime from,
        LocalDateTime to,
        int page,
        int size
    );
}
