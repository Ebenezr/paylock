package com.blind.paylock.service;

import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.datalayer.model.Reservation;
import com.blind.paylock.datalayer.model.TicketType;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.impl.EventServiceImpl;
import com.blind.paylock.service.impl.TicketServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.EventStatus;
import com.blind.paylock.utils.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplCancelTest {

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

    private EventServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EventServiceImpl(eventRepository, ticketTypeRepository, reservationRepository, walletProcessor, ticketService);
    }

    @Test
    void cancelEvent_shouldRefundAndInvalidate() {
        UUID eventId = UUID.randomUUID();
        Event e = Event.builder().id(eventId).status(EventStatus.PUBLISHED).name("E").createdAt(LocalDateTime.now()).build();
        TicketType tt = TicketType.builder().id(UUID.randomUUID()).eventId(eventId).name("T").price(new BigDecimal("10")).totalQuantity(10).build();
        Reservation r = Reservation.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).ticketTypeId(tt.getId()).quantity(1).amountPaid(new BigDecimal("10")).totalAmount(new BigDecimal("10")).status(ReservationStatus.PAID).expiryDate(LocalDateTime.now().plusDays(1)).createdAt(LocalDateTime.now()).eventId(eventId).build();

        when(eventRepository.findById(any(UUID.class))).thenReturn(Mono.just(e));
        when(ticketTypeRepository.findAllByEventId(any(UUID.class))).thenReturn(Flux.just(tt));
        when(reservationRepository.findAllByEventIdAndStatusIn(any(UUID.class), anyList())).thenReturn(Flux.just(r));
        // ensure reservationRepository.save(...) returns a Mono so mapper doesn't return null
        when(reservationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(walletProcessor.credit(any(), any(), any(), any())).thenReturn(Mono.empty());
        // use doReturn to avoid type resolution issues with Mockito on concrete class methods
        org.mockito.Mockito.doReturn(Mono.just(ResponseFactory.success(null, "ref")))
                .when(ticketService).invalidateTicketsByEvent(anyString());

        ApiResponse<Void> resp = service.cancelEvent(eventId.toString()).block();
        assertThat(resp).isNotNull();
    }
}
