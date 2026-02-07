package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.ReservationCreateRequestDto;
import com.blind.paylock.datalayer.dto.request.ReservationPaymentRequestDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ReservationService {

    Mono<ApiResponse> createReservationWithFirstPayment(
        ReservationCreateRequestDto request,
        Map<String, String> headers
    );

    Mono<ApiResponse> makePayment(
        String reservationId,
        ReservationPaymentRequestDto request,
        Map<String, String> headers
    );

    Mono<ApiResponse> cancelReservation(
        String reservationId,
        Map<String, String> headers
    );

    Mono<ApiResponse> getReservation(
        String reservationId,
        Map<String, String> headers
    );

    Mono<ApiResponse> listUserReservations(
        Map<String, String> headers,
        int page,
        int size
    );
}
