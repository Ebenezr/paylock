package com.blind.paylock.service.impl;

import com.blind.paylock.datalayer.dto.request.EventCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.EventResponseDto;
import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.exception.InvalidStateException;
import com.blind.paylock.exception.NotFoundException;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.service.EventService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

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
                .flatMap(saved ->
                        ResponseFactory.success(
                                map(saved),
                                requestRefId
                        )
                );
    }

    @Override
    public Mono<ApiResponse<Void>> publishEvent(String eventId) {
        String requestRefId = ResponseFactory.newRequestRefId();

        return eventRepository.findById(UUID.fromString(eventId))
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found")))
                .flatMap(event -> {
                    if (event.getStatus() != EventStatus.DRAFT) {
                        return Mono.error(new InvalidStateException("Only draft events can be published"));
                    }

                    event.setStatus(EventStatus.PUBLISHED);
                    event.setNew(false);
                    return eventRepository.save(event)
                            .then(ResponseFactory.success(null, requestRefId));
                });
    }

    @Override
    public Mono<ApiResponse<Void>> cancelEvent(String eventId) {
        String requestRefId = ResponseFactory.newRequestRefId();

        return eventRepository.findById(UUID.fromString(eventId))
                .switchIfEmpty(Mono.error(new NotFoundException("Event not found")))
                .flatMap(event -> {
                    event.setStatus(EventStatus.CANCELED);
                    event.setNew(false);
                    return eventRepository.save(event)
                            .then(ResponseFactory.success(null, requestRefId));
                });
    }

    @Override
    public Mono<ApiResponse<List<EventResponseDto>>> listPublishedEvents(int page, int size) {
        String requestRefId = ResponseFactory.newRequestRefId();

        return eventRepository.findAllByStatus(EventStatus.PUBLISHED)
                .map(this::map)
                .collectList()
                .flatMap(list ->
                        ResponseFactory.success(list, requestRefId)
                );
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
