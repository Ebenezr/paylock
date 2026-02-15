package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.TicketTypeCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.TicketTypeResponseDto;
import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.datalayer.model.TicketType;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.impl.TicketTypeServiceImpl;
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
class TicketTypeServiceImplTest {

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private EventRepository eventRepository;

    private TicketTypeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketTypeServiceImpl(ticketTypeRepository, eventRepository);
    }

    @Test
    void createTicketType_whenEventPublished_shouldSave() {
        UUID eventId = UUID.randomUUID();
        Event e = Event.builder().id(eventId).status(com.blind.paylock.utils.enums.EventStatus.PUBLISHED).name("E").createdAt(LocalDateTime.now()).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).paymentCutoff(LocalDateTime.now()).build();
        when(eventRepository.findById(eventId)).thenReturn(Mono.just(e));

        TicketType saved = TicketType.builder().id(UUID.randomUUID()).eventId(eventId).name("T").price(null).totalQuantity(10).createdAt(LocalDateTime.now()).build();
        when(ticketTypeRepository.save(any())).thenReturn(Mono.just(saved));

        TicketTypeCreateRequestDto req = new TicketTypeCreateRequestDto();
        req.setName("T");
        req.setPrice(null);
        req.setQuantity(10);

        ApiResponse<TicketTypeResponseDto> r = service.createTicketType(eventId.toString(), req).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getTicketTypeId()).isEqualTo(saved.getId().toString());
    }

    @Test
    void listTicketTypes_shouldReturnList() {
        UUID eventId = UUID.randomUUID();
        TicketType t = TicketType.builder().id(UUID.randomUUID()).eventId(eventId).name("T").price(null).totalQuantity(5).createdAt(LocalDateTime.now()).build();
        when(ticketTypeRepository.findAllByEventId(eventId)).thenReturn(Flux.just(t));

        ApiResponse<java.util.List<TicketTypeResponseDto>> r = service.listTicketTypes(eventId.toString()).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).hasSize(1);
    }
}
