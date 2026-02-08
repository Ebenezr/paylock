package com.blind.paylock.repository;

import com.blind.paylock.datalayer.model.Ticket;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TicketRepository
        extends ReactiveCrudRepository<Ticket, UUID> {

    Mono<Boolean> existsByReservationId(UUID reservationId);

    Flux<Ticket> findAllByReservationId(UUID reservationId);

    Flux<Ticket> findAllByUserId(UUID userId);

    @Query("""
        SELECT t.* FROM tickets t
        JOIN reservations r ON t.reservation_id = r.id
        WHERE r.event_id = :eventId
    """)
    Flux<Ticket> findAllByEventId(UUID eventId);

    @Modifying
    @Query("""
        UPDATE tickets t
        JOIN reservations r ON t.reservation_id = r.id
        SET t.ticket_status = 'INVALIDATED'
        WHERE r.event_id = :eventId
    """)
    Mono<Integer> invalidateByEventId(UUID eventId);
}
