package com.blind.paylock.service;

import com.blind.paylock.component.TicketIssuer;
import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.datalayer.dto.request.ReservationPaymentRequestDto;
import com.blind.paylock.datalayer.dto.response.ReservationResponseDto;
import com.blind.paylock.datalayer.model.Reservation;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.service.impl.ReservationServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
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
class ReservationServiceImplMakePaymentTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private WalletProcessor walletProcessor;

    @Mock
    private TicketIssuer ticketIssuer;

    private ReservationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReservationServiceImpl(reservationRepository, ticketTypeRepository, eventRepository, walletProcessor, null, ticketIssuer);
    }

    @Test
    void makePayment_whenRemainsPartiallyPaid_shouldNotIssueTickets() {
        UUID userId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();

        Reservation existing = Reservation.builder()
                .id(reservationId)
                .userId(userId)
                .ticketTypeId(UUID.randomUUID())
                .quantity(1)
                .totalAmount(new BigDecimal("10"))
                .amountPaid(new BigDecimal("2"))
                .status(ReservationStatus.PARTIALLY_PAID)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .eventId(UUID.randomUUID())
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Mono.just(existing));
        when(walletProcessor.debit(any(), any(), any(), any())).thenReturn(Mono.empty());
        // reservationRepository.save should return updated reservation
        when(reservationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        ReservationPaymentRequestDto req = new ReservationPaymentRequestDto();
        req.setAmount(new BigDecimal("3"));

        ApiResponse<ReservationResponseDto> r = service.makePayment(reservationId.toString(), req)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                .block();

        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getStatus()).isEqualTo(ReservationStatus.PARTIALLY_PAID.name());
    }
}

