package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.response.EventAvailabilityResponseDto;
import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.datalayer.model.TicketType;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.impl.EventCatalogServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCatalogServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private EventCatalogServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EventCatalogServiceImpl(eventRepository, ticketTypeRepository, reservationRepository);
    }

    @Test
    void availability_shouldReturnData() {
        UUID eventId = UUID.randomUUID();
        Event e = Event.builder().id(eventId).name("E").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).paymentCutoff(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();
        TicketType tt = TicketType.builder().id(UUID.randomUUID()).eventId(eventId).name("T").price(null).totalQuantity(10).build();

        when(eventRepository.findById(any(UUID.class))).thenReturn(Mono.just(e));
        when(ticketTypeRepository.findAllByEventId(any(UUID.class))).thenReturn(Flux.just(tt));
        when(reservationRepository.sumReservedQuantity(any(UUID.class))).thenReturn(Mono.just(2));

        ApiResponse<EventAvailabilityResponseDto> r = service.getEventAvailability(eventId.toString()).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getEventId()).isEqualTo(eventId.toString());
    }
}
