package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.response.TicketResponseDto;
import com.blind.paylock.datalayer.model.Ticket;
import com.blind.paylock.repository.TicketRepository;
import com.blind.paylock.service.impl.TicketServiceImpl;
import com.blind.paylock.utils.apis.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    private TicketServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketServiceImpl(ticketRepository);
    }

    @Test
    void listMyTickets_shouldReturnTickets() {
        UUID userId = UUID.randomUUID();
        Ticket t = new Ticket();
        t.setId(UUID.randomUUID());
        t.setReservationId(UUID.randomUUID());
        t.setTicketTypeId(UUID.randomUUID());
        t.setUserId(userId);
        t.setQrCode("qr");
        t.setStatus(com.blind.paylock.utils.enums.TicketStatus.ISSUED);
        t.setIssuedAt(LocalDateTime.now());

        when(ticketRepository.findAllByUserId(userId)).thenReturn(Flux.just(t));

        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        ApiResponse<List<TicketResponseDto>> r = service.listMyTickets().contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).hasSize(1);
    }

    @Test
    void invalidateTicketsByEvent_shouldReturnSuccess() {
        when(ticketRepository.invalidateByEventId(any())).thenReturn(reactor.core.publisher.Mono.empty());
        ApiResponse<Void> r = service.invalidateTicketsByEvent(UUID.randomUUID().toString()).block();
        assertThat(r).isNotNull();
    }
}
