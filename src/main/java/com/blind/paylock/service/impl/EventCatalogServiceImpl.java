package com.blind.paylock.service.impl;

import com.blind.paylock.component.PaylockLogManager;
import com.blind.paylock.datalayer.dto.response.EventAvailabilityResponseDto;
import com.blind.paylock.datalayer.dto.response.TicketAvailabilityDto;
import com.blind.paylock.exception.NotFoundException;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.EventCatalogService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventCatalogServiceImpl implements EventCatalogService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final ReservationRepository reservationRepository;

    LocalDateTime startTime = LocalDateTime.now();

    public EventCatalogServiceImpl(EventRepository eventRepository, TicketTypeRepository ticketTypeRepository, ReservationRepository reservationRepository) {
        this.eventRepository = eventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Mono<ApiResponse<EventAvailabilityResponseDto>> getEventAvailability(
            String eventId
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();

        UUID eventUuid = UUID.fromString(eventId);

        return eventRepository.findById(eventUuid)
            .switchIfEmpty(Mono.defer(() -> {
                PaylockLogManager.error(
                        requestRefId,
                        "GET_EVENT_AVAILABILITY_ERROR",
                        PaylockLogManager.processDuration(startTime),
                        "EVENT_NOT_FOUND"
                );
                return Mono.error(new NotFoundException("Event not found"));
            }))
            .flatMap(event ->
                    ticketTypeRepository.findAllByEventId(eventUuid)
                            .flatMap(tt ->
                                    reservationRepository
                                            .sumReservedQuantity(tt.getId())
                                            .map(reserved ->
                                                    TicketAvailabilityDto.builder()
                                                            .ticketTypeId(tt.getId().toString())
                                                            .name(tt.getName())
                                                            .price(tt.getPrice())
                                                            .totalQuantity(tt.getTotalQuantity())
                                                            .availableQuantity(
                                                                    tt.getTotalQuantity() - reserved
                                                            )
                                                            .build()
                    ))
                    .collectList()
                    .flatMap(ticketList -> {
                        PaylockLogManager.info(
                                requestRefId,
                                "GET_EVENT_AVAILABILITY",
                                PaylockLogManager.processDuration(startTime),
                                "EVENT_AVAILABILITY_FETCHED"
                        );
                        return ResponseFactory.success(
                                EventAvailabilityResponseDto.builder()
                                        .eventId(event.getId().toString())
                                        .name(event.getName())
                                        .startDate(event.getStartDate())
                                        .endDate(event.getEndDate())
                                        .paymentCutoff(event.getPaymentCutoff())
                                        .ticketTypes(ticketList)
                                        .build(),
                                requestRefId
                        );
                    })
             );
     }
 }
