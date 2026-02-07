package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.ReservationCreateRequestDto;
import com.blind.paylock.datalayer.dto.request.ReservationPaymentRequestDto;
import com.blind.paylock.datalayer.dto.response.ReservationResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReservationService {

    Mono<ApiResponse<ReservationResponseDto>> createReservationWithFirstPayment(
        ReservationCreateRequestDto request
    );

    Mono<ApiResponse<ReservationResponseDto>> makePayment(
        String reservationId,
        ReservationPaymentRequestDto request
    );

    Mono<ApiResponse<Void>> cancelReservation(
        String reservationId

    );

    Mono<ApiResponse<ReservationResponseDto>> getReservation(
        String reservationId
    );

    Mono<ApiResponse<List<ReservationResponseDto>>> listUserReservations(
        int page,
        int size
    );
}
