package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.EventCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.EventResponseDto;
import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.impl.EventServiceImpl;
import com.blind.paylock.service.impl.TicketServiceImpl;
import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.enums.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private WalletProcessor walletProcessor;

    @Mock
    private TicketServiceImpl ticketService;

    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository, ticketTypeRepository, reservationRepository, walletProcessor, ticketService);
    }

    @Test
    void createEvent_shouldSaveAndReturn() {
        EventCreateRequestDto req = new EventCreateRequestDto();
        req.setName("Test");
        req.setStartDate(LocalDateTime.now().plusDays(1));
        req.setEndDate(LocalDateTime.now().plusDays(2));
        req.setPaymentCutoff(LocalDateTime.now().plusDays(1));

        Event saved = Event.builder().id(UUID.randomUUID()).name("Test").startDate(req.getStartDate()).endDate(req.getEndDate()).paymentCutoff(req.getPaymentCutoff()).status(EventStatus.DRAFT).createdAt(LocalDateTime.now()).build();

        when(eventRepository.save(any())).thenReturn(Mono.just(saved));

        EventResponseDto r = eventService.createEvent(req).block().getBody();
        assertThat(r).isNotNull();
        assertThat(r.getName()).isEqualTo("Test");
    }

    @Test
    void publishEvent_whenDraft_shouldPublish() {
        Event e = Event.builder().id(UUID.randomUUID()).status(EventStatus.DRAFT).name("n").createdAt(LocalDateTime.now()).build();
        when(eventRepository.findById(any(java.util.UUID.class))).thenReturn(Mono.just(e));
        when(eventRepository.save(any())).thenReturn(Mono.just(e));

        ApiResponse<Void> r = eventService.publishEvent(UUID.randomUUID().toString()).block();
        assertThat(r).isNotNull();
    }
}
