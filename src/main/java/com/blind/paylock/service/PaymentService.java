package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.PaymentInternalRequestDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface PaymentService {

    Mono<ApiResponse> processPayment(
        PaymentInternalRequestDto request,
        Map<String, String> headers
    );
}
