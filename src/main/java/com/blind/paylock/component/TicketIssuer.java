package com.blind.paylock.component;

import com.blind.paylock.datalayer.model.Reservation;
import com.blind.paylock.datalayer.model.Ticket;
import com.blind.paylock.repository.TicketRepository;
import com.blind.paylock.utils.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TicketIssuer {

    private final TicketRepository ticketRepository;

    public Mono<Void> issueTickets(Reservation reservation) {

        return ticketRepository.existsByReservationId(reservation.getId())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.empty(); // already issued
                }

                List<Ticket> tickets = IntStream
                    .range(0, reservation.getQuantity())
                    .mapToObj(i ->
                        Ticket.builder()
                            .id(UUID.randomUUID())
                            .reservationId(reservation.getId())
                            .ticketTypeId(reservation.getTicketTypeId())
                            .userId(reservation.getUserId())
                            .qrCode(UUID.randomUUID().toString())
                            .status(TicketStatus.ISSUED)
                            .issuedAt(LocalDateTime.now())
                                .isNew(true)
                                .build()
                    )
                    .toList();

                return ticketRepository.saveAll(tickets).then();
            });
    }
}
