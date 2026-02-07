package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.ReservationCreateRequestDto;
import com.blind.paylock.datalayer.dto.request.ReservationPaymentRequestDto;
import com.blind.paylock.datalayer.dto.response.ReservationResponseDto;
import com.blind.paylock.service.ReservationService;
import com.blind.paylock.utils.apis.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public Mono<ApiResponse<ReservationResponseDto>> create(
            @RequestBody ReservationCreateRequestDto request
    ) {
        return reservationService.createReservationWithFirstPayment(request);
    }

    @PostMapping("/{id}/payments")
    public Mono<ApiResponse<ReservationResponseDto>> pay(
            @PathVariable String id,
            @RequestBody ReservationPaymentRequestDto request
    ) {
        return reservationService.makePayment(id, request);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<ReservationResponseDto>> get(
            @PathVariable String id
    ) {
        return reservationService.getReservation(id);
    }

    @GetMapping("/me")
    public Mono<ApiResponse<List<ReservationResponseDto>>> myReservations(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return reservationService.listUserReservations(page, size);
    }
}
