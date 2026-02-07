package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.TicketTypeCreateRequestDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TicketTypeService {

    Mono<ApiResponse> createTicketType(
        String eventId,
        TicketTypeCreateRequestDto request,
        Map<String, String> headers
    );

    Mono<ApiResponse> listTicketTypes(
        String eventId,
        Map<String, String> headers
    );
}
