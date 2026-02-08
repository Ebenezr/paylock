package com.blind.paylock.service.impl;

import com.blind.paylock.datalayer.dto.response.TicketResponseDto;
import com.blind.paylock.repository.TicketRepository;
import com.blind.paylock.service.TicketService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Mono<ApiResponse<List<TicketResponseDto>>> listMyTickets() {
        String requestRefId = ResponseFactory.newRequestRefId();

        return ReactiveSecurityUtil.currentUserId()
            .flatMapMany(userId ->
                ticketRepository.findAllByUserId(userId)
            )
            .map(ticket ->
                TicketResponseDto.builder()
                    .ticketId(ticket.getId().toString())
                    .reservationId(UUID.fromString(ticket.getReservationId().toString()))
                    .ticketTypeId(UUID.fromString(ticket.getTicketTypeId().toString()))
                    .qrCode(ticket.getQrCode())
                    .status(TicketStatus.valueOf(ticket.getStatus().name()))
                    .issuedAt(ticket.getIssuedAt())
                    .build()
            )
            .collectList()
            .flatMap(list ->
                ResponseFactory.success(list, requestRefId)
            );
    }

    @Override
    public Mono<ApiResponse<Void>> invalidateTicketsByEvent(String eventId) {
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }
}
