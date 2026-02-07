package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.EventCreateRequestDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface EventService {

    Mono<ApiResponse> createEvent(
        EventCreateRequestDto request,
        Map<String, String> headers
    );

    Mono<ApiResponse> publishEvent(
        String eventId,
        Map<String, String> headers
    );

    Mono<ApiResponse> cancelEvent(
        String eventId,
        Map<String, String> headers
    );

    Mono<ApiResponse> listPublishedEvents(
        Map<String, String> headers,
        int page,
        int size
    );
}
