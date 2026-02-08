package com.blind.paylock.service.impl;

import com.blind.paylock.component.TicketIssuer;
import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.config.PaylockConfigProperties;
import com.blind.paylock.datalayer.dto.request.ReservationCreateRequestDto;
import com.blind.paylock.datalayer.dto.request.ReservationPaymentRequestDto;
import com.blind.paylock.datalayer.dto.response.ReservationResponseDto;
import com.blind.paylock.datalayer.model.Reservation;
import com.blind.paylock.exception.InvalidStateException;
import com.blind.paylock.exception.NotFoundException;
import com.blind.paylock.repository.EventRepository;
import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.repository.TicketTypeRepository;
import com.blind.paylock.service.ReservationService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.blind.paylock.utils.enums.EventStatus;
import com.blind.paylock.utils.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;
    private final WalletProcessor walletProcessor;
    private final PaylockConfigProperties properties;
    private final TicketIssuer ticketIssuer;


    @Override
    public Mono<ApiResponse<ReservationResponseDto>> createReservationWithFirstPayment(
            ReservationCreateRequestDto request
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();

        return ReactiveSecurityUtil.currentUserId()
            .flatMap(userId ->
                ticketTypeRepository.findById(UUID.fromString(request.getTicketTypeId()))
                    .switchIfEmpty(Mono.error(new NotFoundException("Ticket type not found")))
                    .flatMap(ticketType ->
                        eventRepository.findById(ticketType.getEventId())
                            .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                            .switchIfEmpty(Mono.error(new InvalidStateException("Event not published")))
                            .flatMap(event -> {

                                BigDecimal total =
                                    ticketType.getPrice()
                                        .multiply(BigDecimal.valueOf(request.getQuantity()));

                                BigDecimal thresholdAmount =
                                        total
                                                .multiply(BigDecimal.valueOf(Long.parseLong(properties.getLockThresholdPercent())))
                                                .divide(BigDecimal.valueOf(100));

                                boolean meetsThreshold =
                                        request.getFirstPayment().compareTo(thresholdAmount) >= 0;

                                if (!meetsThreshold) {
                                    return Mono.error(
                                            new InvalidStateException(
                                                    "Minimum payment to reserve tickets is "
                                                            + properties.getLockThresholdPercent() + "% of total"
                                            )
                                    );
                                }


                                if (request.getFirstPayment().compareTo(total) > 0) {
                                    return Mono.error(new InvalidStateException("Overpayment not allowed"));
                                }

                                UUID reservationId = UUID.randomUUID();

                                // Check user's existing tickets for this event
                                return reservationRepository
                                        .sumUserTicketsForEvent(userId, event.getId())
                                        .flatMap(existingTickets -> {
                                            int totalRequestedTickets = existingTickets + request.getQuantity();

                                            if (totalRequestedTickets > 3) {
                                                int remainingAllowed = 3 - existingTickets;
                                                return Mono.error(
                                                        new InvalidStateException(
                                                                "Maximum 3 tickets allowed per event. You already have "
                                                                        + existingTickets + " ticket(s). You can only purchase "
                                                                        + remainingAllowed + " more."
                                                        )
                                                );
                                            }

                                            return reservationRepository
                                                    .sumReservedQuantity(ticketType.getId())
                                                    .flatMap(reserved -> {

                                                        int available =
                                                                ticketType.getTotalQuantity() - reserved;

                                                        if (available < request.getQuantity()) {
                                                            return Mono.error(
                                                                    new InvalidStateException("Not enough tickets available")
                                                            );
                                                        }

                                                        return walletProcessor.debit(
                                                                userId,
                                                                request.getFirstPayment(),
                                                                "RESERVATION",
                                                                reservationId.toString()
                                                        ).then(
                                                                reservationRepository.save(
                                                                        Reservation.builder()
                                                                                .id(reservationId)
                                                                                .userId(userId)
                                                                                .ticketTypeId(ticketType.getId())
                                                                                .quantity(request.getQuantity())
                                                                                .totalAmount(total)
                                                                                .amountPaid(request.getFirstPayment())
                                                                                .status(
                                                                                        request.getFirstPayment().compareTo(total) == 0
                                                                                                ? ReservationStatus.PAID
                                                                                                : ReservationStatus.PARTIALLY_PAID
                                                                                )
                                                                                .expiryDate(LocalDateTime.now().plusDays(7))
                                                                                .createdAt(LocalDateTime.now())
                                                                                .build()
                                                                )
                                                        );
                                                    });
                                        });

                            })
                    )
            )
            .flatMap(r ->
                ResponseFactory.success(map(r), requestRefId)
            );
    }

    @Override
    public Mono<ApiResponse<ReservationResponseDto>> makePayment(
            String reservationId,
            ReservationPaymentRequestDto request
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();
        UUID reservationUuid = UUID.fromString(reservationId);

        return ReactiveSecurityUtil.currentUserId()
            .flatMap(userId ->
                reservationRepository.findById(reservationUuid)
                    .filter(r -> r.getUserId().equals(userId))
                    .switchIfEmpty(Mono.error(new NotFoundException("Reservation not found")))
                    .flatMap(r -> {

                        if (r.getStatus() != ReservationStatus.PARTIALLY_PAID) {
                            return Mono.error(new InvalidStateException("Invalid reservation state"));
                        }

                        BigDecimal newPaid =
                            r.getAmountPaid().add(request.getAmount());

                        if (newPaid.compareTo(r.getTotalAmount()) > 0) {
                            return Mono.error(new InvalidStateException("Overpayment not allowed"));
                        }

                        boolean becomesPaid =
                                newPaid.compareTo(r.getTotalAmount()) == 0;

                        Reservation updated =
                                r.toBuilder()
                                        .amountPaid(newPaid)
                                        .status(
                                                becomesPaid
                                                        ? ReservationStatus.PAID
                                                        : ReservationStatus.PARTIALLY_PAID
                                        )
                                        .isNew(false)
                                        .build();

                        return walletProcessor.debit(
                                userId,
                                request.getAmount(),
                                "RESERVATION_PAYMENT",
                                reservationId
                        ).then(reservationRepository.save(updated))
                                .flatMap(saved -> {
                                    if (becomesPaid) {
                                        return ticketIssuer
                                                .issueTickets(saved)
                                                .thenReturn(saved);
                                    }
                                    return Mono.just(saved);
                                });
                    })
            )
                .flatMap(r ->
                        ResponseFactory.success(map(r), requestRefId)
                );
    }

    @Override
    public Mono<ApiResponse<Void>> cancelReservation(String reservationId) {
        // Phase 2 – refund logic
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }

    @Override
    public Mono<ApiResponse<ReservationResponseDto>> getReservation(
            String reservationId
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();
        UUID reservationUuid = UUID.fromString(reservationId);

        return ReactiveSecurityUtil.currentUserId()
            .flatMap(userId ->
                reservationRepository.findById(reservationUuid)
                    .filter(r -> r.getUserId().equals(userId))
            )
            .flatMap(r ->
                ResponseFactory.success(map(r), requestRefId)
            )
            .switchIfEmpty(
                ResponseFactory.errorMono(
                    HttpStatus.NOT_FOUND,
                    "RESERVATION_NOT_FOUND",
                    "Reservation not found",
                    requestRefId
                )
            );
    }

    @Override
    public Mono<ApiResponse<List<ReservationResponseDto>>> listUserReservations(
            int page,
            int size
    ) {
        String requestRefId = ResponseFactory.newRequestRefId();

        return ReactiveSecurityUtil.currentUserId()
            .flatMapMany(userId ->
                reservationRepository.findAllByUserId(userId)
            )
            .map(this::map)
            .collectList()
            .flatMap(list ->
                ResponseFactory.success(list, requestRefId)
            );
    }

    private ReservationResponseDto map(Reservation r) {
        return ReservationResponseDto.builder()
            .reservationId(r.getId().toString())
            .ticketTypeId(r.getTicketTypeId().toString())
            .quantity(r.getQuantity())
            .totalAmount(r.getTotalAmount())
            .amountPaid(r.getAmountPaid())
            .status(r.getStatus().name())
            .expiryDate(r.getExpiryDate())
            .build();
    }
}
