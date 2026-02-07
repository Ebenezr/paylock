package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.response.EventAvailabilityResponseDto;
import com.blind.paylock.service.EventCatalogService;
import com.blind.paylock.utils.apis.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventCatalogController {

    private final EventCatalogService eventCatalogService;

    @GetMapping("/{eventId}/availability")
    public Mono<ApiResponse<EventAvailabilityResponseDto>> availability(
            @PathVariable String eventId
    ) {
        return eventCatalogService.getEventAvailability(eventId);
    }
}
