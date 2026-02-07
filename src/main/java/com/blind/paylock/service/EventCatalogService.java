package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.response.EventAvailabilityResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

public interface EventCatalogService {

    Mono<ApiResponse<EventAvailabilityResponseDto>> getEventAvailability(
        String eventId
    );
}
