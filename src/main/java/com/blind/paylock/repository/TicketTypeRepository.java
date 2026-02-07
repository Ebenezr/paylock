package com.blind.paylock.repository;

import com.blind.paylock.datalayer.model.TicketType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TicketTypeRepository
        extends ReactiveCrudRepository<TicketType, UUID> {

    Flux<TicketType> findAllByEventId(UUID eventId);
}
