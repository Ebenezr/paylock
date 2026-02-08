package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.response.TicketResponseDto;
import com.blind.paylock.service.TicketService;
import com.blind.paylock.utils.apis.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/me")
    public Mono<ApiResponse<List<TicketResponseDto>>> listMyTickets() {
        return ticketService.listMyTickets();
    }
}
