package com.blind.paylock.service;

import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TicketService {

    Mono<ApiResponse> listMyTickets(
        Map<String, String> headers
    );

    Mono<ApiResponse> invalidateTicketsByEvent(
        String eventId,
        Map<String, String> headers
    );
}
