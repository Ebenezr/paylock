package com.blind.paylock.service;

import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.enums.RefundReason;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface RefundService {

    Mono<ApiResponse> refundReservation(
        String reservationId,
        RefundReason reason,
        Map<String, String> headers
    );
}
