package com.blind.paylock.service;

import com.blind.paylock.component.TicketIssuer;
import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.config.PaylockConfigProperties;
import com.blind.paylock.datalayer.dto.request.ReservationCreateRequestDto;
import com.blind.paylock.datalayer.dto.request.ReservationPaymentRequestDto;
import com.blind.paylock.datalayer.dto.response.ReservationResponseDto;
import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.datalayer.model.Reservation;
import com.blind.paylock.datalayer.model.TicketType;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.impl.ReservationServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.enums.EventStatus;
import com.blind.paylock.utils.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private WalletProcessor walletProcessor;

    @Mock
    private PaylockConfigProperties properties;

    @Mock
    private TicketIssuer ticketIssuer;

    private ReservationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReservationServiceImpl(reservationRepository, ticketTypeRepository, eventRepository, walletProcessor, properties, ticketIssuer);
    }

    @Test
    void createReservationWithFirstPayment_happyPath() {
        UUID userId = UUID.randomUUID();
        UUID ticketTypeId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        ReservationCreateRequestDto req = new ReservationCreateRequestDto();
        req.setTicketTypeId(ticketTypeId.toString());
        req.setQuantity(1);
        req.setFirstPayment(new BigDecimal("5"));

        TicketType tt = TicketType.builder().id(ticketTypeId).eventId(eventId).price(new BigDecimal("5")).totalQuantity(10).name("T").build();
        Event e = Event.builder().id(eventId).status(EventStatus.PUBLISHED).name("E").createdAt(LocalDateTime.now()).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).paymentCutoff(LocalDateTime.now()).build();

        when(ticketTypeRepository.findById(ticketTypeId)).thenReturn(Mono.just(tt));
        when(eventRepository.findById(eventId)).thenReturn(Mono.just(e));
        when(properties.getLockThresholdPercent()).thenReturn("50");
        when(reservationRepository.sumUserTicketsForEvent(any(), any())).thenReturn(Mono.just(0));
        when(reservationRepository.sumReservedQuantity(ticketTypeId)).thenReturn(Mono.just(0));
        when(walletProcessor.debit(any(), any(), any(), any())).thenReturn(Mono.empty());
        when(reservationRepository.save(any())).thenReturn(Mono.just(Reservation.builder().id(UUID.randomUUID()).userId(userId).ticketTypeId(ticketTypeId).quantity(1).totalAmount(new BigDecimal("5")).amountPaid(new BigDecimal("5")).status(ReservationStatus.PAID).expiryDate(LocalDateTime.now().plusDays(7)).createdAt(LocalDateTime.now()).eventId(eventId).build()));

        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        ApiResponse<ReservationResponseDto> r = service.createReservationWithFirstPayment(req).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
    }

    @Test
    void makePayment_whenBecomesPaid_shouldIssueTickets() {
        UUID userId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        Reservation existing = Reservation.builder().id(reservationId).userId(userId).ticketTypeId(UUID.randomUUID()).quantity(1).totalAmount(new BigDecimal("10")).amountPaid(new BigDecimal("5")).status(ReservationStatus.PARTIALLY_PAID).expiryDate(LocalDateTime.now().plusDays(7)).createdAt(LocalDateTime.now()).eventId(UUID.randomUUID()).build();

        when(reservationRepository.findById(reservationId)).thenReturn(Mono.just(existing));
        when(walletProcessor.debit(any(), any(), any(), any())).thenReturn(Mono.empty());
        when(reservationRepository.save(any())).thenReturn(Mono.just(existing));
        when(ticketIssuer.issueTickets(any())).thenReturn(Mono.empty());

        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        ReservationPaymentRequestDto req = new ReservationPaymentRequestDto();
        req.setAmount(new BigDecimal("5"));

        ApiResponse<ReservationResponseDto> r = service.makePayment(reservationId.toString(), req).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
    }
}
