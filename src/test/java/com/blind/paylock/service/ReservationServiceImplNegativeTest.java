//package com.blind.paylock.service;
//
//import com.blind.paylock.component.TicketIssuer;
//import com.blind.paylock.component.WalletProcessor;
//import com.blind.paylock.config.PaylockConfigProperties;
//import com.blind.paylock.datalayer.dto.request.ReservationCreateRequestDto;
//import com.blind.paylock.datalayer.model.Event;
//import com.blind.paylock.datalayer.model.TicketType;
//import com.blind.paylock.exception.InvalidStateException;
//import com.blind.paylock.repository.EventRepository;
//import com.blind.paylock.repository.ReservationRepository;
//import com.blind.paylock.repository.TicketTypeRepository;
//import com.blind.paylock.service.impl.ReservationServiceImpl;
//import com.blind.paylock.utils.enums.EventStatus;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ReservationServiceImplNegativeTest {
//
//    @Mock
//    private ReservationRepository reservationRepository;
//
//    @Mock
//    private TicketTypeRepository ticketTypeRepository;
//
//    @Mock
//    private EventRepository eventRepository;
//
//    @Mock
//    private WalletProcessor walletProcessor;
//
//    @Mock
//    private PaylockConfigProperties properties;
//
//    @Mock
//    private TicketIssuer ticketIssuer;
//
//    private ReservationServiceImpl service;
//
//    @BeforeEach
//    void setUp() {
//        service = new ReservationServiceImpl(reservationRepository, ticketTypeRepository, eventRepository, walletProcessor, properties, ticketIssuer);
//    }
//
//    @Test
//    void createReservation_whenOverpayment_shouldThrowInvalidState() {
//        UUID userId = UUID.randomUUID();
//        UUID ticketTypeId = UUID.randomUUID();
//        UUID eventId = UUID.randomUUID();
//
//        ReservationCreateRequestDto req = new ReservationCreateRequestDto();
//        req.setTicketTypeId(ticketTypeId.toString());
//        req.setQuantity(1);
//        req.setFirstPayment(new BigDecimal("100"));
//
//        TicketType tt = TicketType.builder().id(ticketTypeId).eventId(eventId).price(new BigDecimal("5")).totalQuantity(10).name("T").build();
//        Event e = Event.builder().id(eventId).status(EventStatus.PUBLISHED).name("E").createdAt(LocalDateTime.now()).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).paymentCutoff(LocalDateTime.now()).build();
//
//        when(ticketTypeRepository.findById(ticketTypeId)).thenReturn(Mono.just(tt));
//        when(eventRepository.findById(eventId)).thenReturn(Mono.just(e));
//        when(properties.getLockThresholdPercent()).thenReturn("50");
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));
//
//        Mono<?> flow = service.createReservationWithFirstPayment(req).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
//
//        StepVerifier.create(flow)
//                .expectError(InvalidStateException.class)
//                .verify();
//    }
//}
