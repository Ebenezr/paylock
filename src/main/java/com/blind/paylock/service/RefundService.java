package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.response.RefundResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.enums.RefundReason;
import reactor.core.publisher.Mono;

public interface RefundService {

    Mono<ApiResponse<RefundResponseDto>> refundReservation(
        String reservationId,
        RefundReason reason
    );
}
