package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.response.TicketResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TicketService {

    Mono<ApiResponse<List<TicketResponseDto>>>  listMyTickets(
    );

    Mono<ApiResponse<Void>>  invalidateTicketsByEvent(
        String eventId
    );
}
