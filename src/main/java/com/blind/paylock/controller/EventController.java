package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.EventCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.EventResponseDto;
import com.blind.paylock.service.EventService;
import com.blind.paylock.utils.apis.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public Mono<ApiResponse<EventResponseDto>> create(
            @Valid @RequestBody EventCreateRequestDto request
    ) {
        return eventService.createEvent(request);
    }

    @PostMapping("/{eventId}/publish")
    public Mono<ApiResponse<Void>> publish(
            @PathVariable String eventId
    ) {
        return eventService.publishEvent(eventId);
    }

    @PostMapping("/{eventId}/cancel")
    public Mono<ApiResponse<Void>> cancel(
            @PathVariable String eventId
    ) {
        return eventService.cancelEvent(eventId);
    }

    @GetMapping
    public Mono<ApiResponse<List<EventResponseDto>>> listPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return eventService.listPublishedEvents(page, size);
    }
}
