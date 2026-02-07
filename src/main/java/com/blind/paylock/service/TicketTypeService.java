package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.TicketTypeCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.TicketTypeResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TicketTypeService {

    Mono<ApiResponse<TicketTypeResponseDto>>  createTicketType(
        String eventId,
        TicketTypeCreateRequestDto request
    );

    Mono<ApiResponse<List<TicketTypeResponseDto>>> listTicketTypes(
        String eventId
    );
}
