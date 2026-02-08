package com.blind.paylock.repository;

import com.blind.paylock.datalayer.model.Ticket;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TicketRepository
        extends ReactiveCrudRepository<Ticket, UUID> {

    Mono<Boolean> existsByReservationId(UUID reservationId);

    Flux<Ticket> findAllByReservationId(UUID reservationId);

    Flux<Ticket> findAllByUserId(UUID userId);

}
