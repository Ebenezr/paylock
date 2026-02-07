package com.blind.paylock.repository;

import com.blind.paylock.datalayer.model.Event;
import com.blind.paylock.utils.enums.EventStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EventRepository
        extends ReactiveCrudRepository<Event, UUID> {

    Flux<Event> findAllByStatus(EventStatus status);
}
