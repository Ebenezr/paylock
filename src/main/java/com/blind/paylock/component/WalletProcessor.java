package com.blind.paylock.component;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletProcessor {

    Mono<Void> debit(
        UUID userId,
        BigDecimal amount,
        String referenceType,
        String referenceId
    );

    Mono<Void> credit(
        UUID userId,
        BigDecimal amount,
        String referenceType,
        String referenceId
    );
}
