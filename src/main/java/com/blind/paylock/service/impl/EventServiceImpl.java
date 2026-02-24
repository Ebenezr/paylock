package com.blind.paylock.service.impl;

import com.blind.paylock.component.PaylockLogManager;
import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.datalayer.dto.request.EventCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.EventListItemResponseDto;
import com.blind.paylock.datalayer.dto.response.EventResponseDto;
import com.blind.paylock.datalayer.dto.response.TicketAvailabilityDto;
import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.exception.InvalidStateException;
import com.blind.paylock.exception.NotFoundException;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.EventService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.EventStatus;
import com.blind.paylock.utils.enums.ReservationStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final ReservationRepository reservationRepository;
    private final WalletProcessor walletProcessor;
    private final TicketServiceImpl ticketService;

    LocalDateTime startTime = LocalDateTime.now();

    public EventServiceImpl(EventRepository eventRepository, TicketTypeRepository ticketTypeRepository, ReservationRepository reservationRepository, WalletProcessor walletProcessor, TicketServiceImpl ticketService) {
        this.eventRepository = eventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.reservationRepository = reservationRepository;
        this.walletProcessor = walletProcessor;
        this.ticketService = ticketService;
    }


    @Override
    public Mono<ApiResponse<EventResponseDto>> createEvent(
            EventCreateRequestDto request
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();

        Event event = Event.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .paymentCutoff(request.getPaymentCutoff())
                .status(EventStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build();

        return eventRepository.save(event)
                .flatMap(saved -> {
                    PaylockLogManager.info(
                            requestRefId,
                            "CREATE_EVENT",
                            PaylockLogManager.processDuration(startTime),
                            "EVENT_CREATED"
                    );
                    return ResponseFactory.success(
                            map(saved),
                            requestRefId
                    );
                });
    }

    @Override
    public Mono<ApiResponse<Void>> publishEvent(String eventId) {
        String requestRefId = ResponseFactory.newRequestRefId();

        return eventRepository.findById(UUID.fromString(eventId))
                .switchIfEmpty(Mono.defer(() -> {
                    PaylockLogManager.error(
                            requestRefId,
                            "PUBLISH_EVENT_ERROR",
                            PaylockLogManager.processDuration(startTime),
                            "EVENT_NOT_FOUND"
                    );
                    return Mono.error(new NotFoundException("Event not found"));
                }))
                .flatMap(event -> {
                    if (event.getStatus() != EventStatus.DRAFT) {
                        PaylockLogManager.error(
                                requestRefId,
                                "PUBLISH_EVENT_ERROR",
                                PaylockLogManager.processDuration(startTime),
                                "EVENT_NOT_DRAFT"
                        );
                        return Mono.error(new InvalidStateException("Only draft events can be published"));
                    }

                    event.setStatus(EventStatus.PUBLISHED);
                    event.setNew(false);
                    return eventRepository.save(event)
                            .then(ResponseFactory.<Void>success(null, requestRefId))
                            .doOnSuccess(ignored -> PaylockLogManager.info(
                                    requestRefId,
                                    "PUBLISH_EVENT",
                                    PaylockLogManager.processDuration(startTime),
                                    "EVENT_PUBLISHED"
                            ));
                });
    }

    @Override
    public Mono<ApiResponse<Void>> cancelEvent(String eventId) {
        String requestRefId = ResponseFactory.newRequestRefId();
        UUID eventUuid = UUID.fromString(eventId);

        return eventRepository.findById(eventUuid)
                .switchIfEmpty(Mono.defer(() -> {
                    PaylockLogManager.error(
                            requestRefId,
                            "CANCEL_EVENT_ERROR",
                            PaylockLogManager.processDuration(startTime),
                            "EVENT_NOT_FOUND"
                    );
                    return Mono.error(new NotFoundException("Event not found"));
                }))
                .flatMap(event -> {

                    if (event.getStatus() == EventStatus.CANCELED) {
                        return Mono.empty();
                    }

                    return eventRepository.save(
                            event.toBuilder()
                                    .status(EventStatus.CANCELED)
                                    .isNew(false)
                                    .build()
                    );
                }).then(refundReservationsByEvent(eventUuid))
                .then(ticketService.invalidateTicketsByEvent(eventId))
                .then(ResponseFactory.<Void>success(null, requestRefId))
                .doOnSuccess(ignored -> PaylockLogManager.info(
                        requestRefId,
                        "CANCEL_EVENT",
                        PaylockLogManager.processDuration(startTime),
                        "EVENT_CANCELLED"
                ))
                .doOnError(err -> PaylockLogManager.error(
                        requestRefId,
                        "CANCEL_EVENT_ERROR",
                        PaylockLogManager.processDuration(startTime),
                        err.getMessage()
                ));
    }

    private Mono<Void> refundReservationsByEvent(UUID eventId) {

        return reservationRepository
                 .findAllByEventIdAndStatusIn(
                         eventId,
                         List.of(
                                 ReservationStatus.PAID,
                                 ReservationStatus.PARTIALLY_PAID
                         )
                 )
                 .flatMap(reservation -> {

                     BigDecimal refundAmount = reservation.getAmountPaid();

                     return walletProcessor.credit(
                                     reservation.getUserId(),
                                     refundAmount,
                                     "EVENT_CANCEL",
                                     reservation.getId().toString()
                             )
                             .then(
                                     reservationRepository.save(
                                             reservation.toBuilder()
                                                     .status(ReservationStatus.CANCELED_BY_EVENT)
                                                     .isNew(false)
                                                     .build()
                                     )
                             );
                 })
                 .then();
     }



    @Override
    public Mono<ApiResponse<List<EventListItemResponseDto>>> listPublishedEvents() {
        String requestRefId = ResponseFactory.newRequestRefId();

        return eventRepository.findAllByStatus(EventStatus.PUBLISHED)
                 .flatMap(event ->
                         ticketTypeRepository.findAllByEventId(event.getId())
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
                                 .map(ticketTypes ->
                                         EventListItemResponseDto.builder()
                                                 .eventId(event.getId().toString())
                                                 .name(event.getName())
                                                 .startDate(event.getStartDate())
                                                 .endDate(event.getEndDate())
                                                 .paymentCutoff(event.getPaymentCutoff())
                                                 .ticketTypes(ticketTypes)
                                                 .build()
                                 )
                 )
                 .collectList()
                .flatMap(list -> {
                        PaylockLogManager.info(
                                requestRefId,
                                "LIST_PUBLISHED_EVENTS",
                                PaylockLogManager.processDuration(startTime),
                                "PUBLISHED_EVENTS_LISTED"
                        );
                        return ResponseFactory.success(list, requestRefId);
                });
    }


    private EventResponseDto map(Event event) {
        return EventResponseDto.builder()
                .eventId(event.getId().toString())
                .name(event.getName())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .paymentCutoff(event.getPaymentCutoff())
                .status(event.getStatus().name())
                .build();
    }
}
