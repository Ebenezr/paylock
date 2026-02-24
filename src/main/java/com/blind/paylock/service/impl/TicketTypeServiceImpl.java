package com.blind.paylock.service.impl;

import com.blind.paylock.component.PaylockLogManager;
import com.blind.paylock.datalayer.dto.request.TicketTypeCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.TicketTypeResponseDto;
import com.blind.paylock.datalayer.model.TicketType;
import com.blind.paylock.exception.InvalidStateException;
import com.blind.paylock.exception.NotFoundException;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.TicketTypeService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.EventStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;

    LocalDateTime startTime = LocalDateTime.now();

    public TicketTypeServiceImpl(TicketTypeRepository ticketTypeRepository, EventRepository eventRepository) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Mono<ApiResponse<TicketTypeResponseDto>> createTicketType(
            String eventId,
            TicketTypeCreateRequestDto request
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();
        UUID eventUuid = UUID.fromString(eventId);

        return eventRepository.findById(eventUuid)
            .switchIfEmpty(Mono.defer(() -> {
                PaylockLogManager.error(
                        requestRefId,
                        "CREATE_TICKET_TYPE_ERROR",
                        PaylockLogManager.processDuration(startTime),
                        "EVENT_NOT_FOUND"
                );
                return Mono.error(new NotFoundException("Event not found"));
            }))
            .flatMap(event -> {
                if (event.getStatus() != EventStatus.PUBLISHED) {
                    PaylockLogManager.error(
                            requestRefId,
                            "CREATE_TICKET_TYPE_ERROR",
                            PaylockLogManager.processDuration(startTime),
                            "EVENT_NOT_PUBLISHED"
                    );
                    return Mono.error(new InvalidStateException("Ticket types can only be created for published events"));
                }

                TicketType tt = TicketType.builder()
                        .id(UUID.randomUUID())
                        .eventId(eventUuid)
                        .name(request.getName())
                        .price(request.getPrice())
                        .totalQuantity(request.getQuantity())
                        .createdAt(LocalDateTime.now())
                        .build();

                return ticketTypeRepository.save(tt)
                        .flatMap(saved -> {
                            PaylockLogManager.info(
                                    requestRefId,
                                    "CREATE_TICKET_TYPE",
                                    PaylockLogManager.processDuration(startTime),
                                    "TICKET_TYPE_CREATED"
                            );
                            return ResponseFactory.success(map(saved), requestRefId);
                        });
             });
     }

     @Override
     public Mono<ApiResponse<List<TicketTypeResponseDto>>> listTicketTypes(
             String eventId
     ) {
         String requestRefId = ResponseFactory.newRequestRefId();
         LocalDateTime startTime = LocalDateTime.now();

         return ticketTypeRepository.findAllByEventId(UUID.fromString(eventId))
                 .map(this::map)
                 .collectList()
                 .flatMap(list -> {
                        PaylockLogManager.info(
                                requestRefId,
                                "LIST_TICKET_TYPES",
                                PaylockLogManager.processDuration(startTime),
                                "TICKET_TYPES_LISTED"
                        );
                        return ResponseFactory.success(list, requestRefId);
                 });
     }

     private TicketTypeResponseDto map(TicketType tt) {
         return TicketTypeResponseDto.builder()
                 .ticketTypeId(tt.getId().toString())
                 .eventId(tt.getEventId().toString())
                 .name(tt.getName())
                 .price(tt.getPrice())
                 .totalQuantity(tt.getTotalQuantity())
                 .build();
     }
 }
