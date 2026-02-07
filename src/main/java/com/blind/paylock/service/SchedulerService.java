package com.blind.paylock.service;

import reactor.core.publisher.Mono;

public interface SchedulerService {

    Mono<Void> processExpiredReservations();

    Mono<Void> processPendingRefunds();
}
