package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.PaymentInternalRequestDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

public interface PaymentService {

    Mono<ApiResponse<Void>> processPayment(
        PaymentInternalRequestDto request
    );
}
