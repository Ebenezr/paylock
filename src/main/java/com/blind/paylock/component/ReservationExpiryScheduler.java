package com.blind.paylock.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryScheduler {

    private final ReservationExpiryProcessor expiryProcessor;

    @Scheduled(fixedDelayString = "${paylock.expiry-check-interval:300000}")
    public void runExpiry() {
        expiryProcessor.expireReservations()
            .doOnSubscribe(s ->
                log.info("Reservation expiry job started")
            )
            .doOnSuccess(v ->
                log.info("Reservation expiry job completed")
            )
            .doOnError(e ->
                log.error("Reservation expiry job failed", e)
            )
            .subscribe();
    }
}
