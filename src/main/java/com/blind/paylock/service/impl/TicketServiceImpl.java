package com.blind.paylock.service.impl;

import com.blind.paylock.component.PaylockLogManager;
import com.blind.paylock.datalayer.dto.response.TicketResponseDto;
import com.blind.paylock.repository.TicketRepository;
import com.blind.paylock.service.TicketService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.TicketStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    LocalDateTime startTime = LocalDateTime.now();

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

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
            .flatMap(list -> {
                PaylockLogManager.info(
                        requestRefId,
                        "LIST_MY_TICKETS",
                        PaylockLogManager.processDuration(startTime),
                        "MY_TICKETS_LISTED"
                );
                return ResponseFactory.success(list, requestRefId);
            });
    }

    @Override
    public Mono<ApiResponse<Void>> invalidateTicketsByEvent(String eventId) {
        String requestRefId = ResponseFactory.newRequestRefId();
        UUID eventUuid = UUID.fromString(eventId);

        return ticketRepository.invalidateByEventId(eventUuid)
                .then(ResponseFactory.<Void>success(null, requestRefId))
                .doOnSuccess(ignored -> PaylockLogManager.info(
                        requestRefId,
                        "INVALIDATE_TICKETS_BY_EVENT",
                        PaylockLogManager.processDuration(startTime),
                        "TICKETS_INVALIDATED"
                ))
                .doOnError(err -> PaylockLogManager.error(
                        requestRefId,
                        "INVALIDATE_TICKETS_BY_EVENT_ERROR",
                        PaylockLogManager.processDuration(startTime),
                        err.getMessage()
                ));
    }
}
