package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.TicketTypeCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.TicketTypeResponseDto;
import com.blind.paylock.service.TicketTypeService;
import com.blind.paylock.utils.apis.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Mono<ApiResponse<TicketTypeResponseDto>> create(
            @PathVariable String eventId,
            @Valid @RequestBody TicketTypeCreateRequestDto request
    ) {
        return ticketTypeService.createTicketType(eventId, request);
    }

    @GetMapping
    public Mono<ApiResponse<List<TicketTypeResponseDto>>> list(
            @PathVariable String eventId
    ) {
        return ticketTypeService.listTicketTypes(eventId);
    }
}
