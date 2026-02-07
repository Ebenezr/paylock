package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.EventCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.EventResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventService {

    Mono<ApiResponse<EventResponseDto>> createEvent(
        EventCreateRequestDto request
    );

    Mono<ApiResponse<Void>> publishEvent(
        String eventId
    );

    Mono<ApiResponse<Void>> cancelEvent(
        String eventId
    );

    Mono<ApiResponse<List<EventResponseDto>>> listPublishedEvents(
        int page,
        int size
    );
}
