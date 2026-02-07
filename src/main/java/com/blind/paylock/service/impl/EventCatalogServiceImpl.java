package com.blind.paylock.service.impl;

import com.blind.paylock.datalayer.dto.response.EventAvailabilityResponseDto;
import com.blind.paylock.datalayer.dto.response.TicketAvailabilityDto;
import com.blind.paylock.exception.NotFoundException;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.EventCatalogService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventCatalogServiceImpl implements EventCatalogService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Override
    public Mono<ApiResponse<EventAvailabilityResponseDto>> getEventAvailability(
            String eventId
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();
        UUID eventUuid = UUID.fromString(eventId);

        return eventRepository.findById(eventUuid)
            .switchIfEmpty(Mono.error(new NotFoundException("Event not found")))
            .flatMap(event ->
                ticketTypeRepository.findAllByEventId(eventUuid)
                    .map(tt ->
                        TicketAvailabilityDto.builder()
                            .ticketTypeId(tt.getId().toString())
                            .name(tt.getName())
                            .price(tt.getPrice())
                            .totalQuantity(tt.getTotalQuantity())
                            // BEFORE reservations: all available
                            .availableQuantity(tt.getTotalQuantity())
                            .build()
                    )
                    .collectList()
                    .flatMap(ticketList ->
                        ResponseFactory.success(
                            EventAvailabilityResponseDto.builder()
                                .eventId(event.getId().toString())
                                .name(event.getName())
                                .startDate(event.getStartDate())
                                .endDate(event.getEndDate())
                                .paymentCutoff(event.getPaymentCutoff())
                                .ticketTypes(ticketList)
                                .build(),
                            requestRefId
                        )
                    )
            );
    }
}
