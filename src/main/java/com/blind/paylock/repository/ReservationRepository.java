package com.blind.paylock.repository;

import com.blind.paylock.datalayer.model.Reservation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReservationRepository
        extends ReactiveCrudRepository<Reservation, UUID> {

    Flux<Reservation> findAllByUserId(UUID userId);

    @Query("""
        SELECT COALESCE(SUM(r.quantity), 0)
        FROM reservations r
        JOIN ticket_types tt ON r.ticket_type_id = tt.id
        WHERE r.user_id = :userId
          AND tt.event_id = :eventId
          AND r.status IN ('PARTIALLY_PAID', 'PAID')
    """)
    Mono<Integer> sumUserTicketsForEvent(UUID userId, UUID eventId);

    @Query("""
        SELECT COALESCE(SUM(quantity), 0)
        FROM reservations
        WHERE ticket_type_id = :ticketTypeId
          AND status IN ('PARTIALLY_PAID', 'PAID')
    """)
    Mono<Integer> sumReservedQuantity(UUID ticketTypeId);

    @Query("""
    SELECT COALESCE(SUM(quantity), 0)
    FROM reservations
    WHERE ticket_type_id = :ticketTypeId
      AND status IN ('PARTIALLY_PAID', 'PAID')
      AND amount_paid >= (total_amount * :threshold / 100)
""")
    Mono<Integer> sumLockedQuantity(
            UUID ticketTypeId,
            int threshold
    );

}
