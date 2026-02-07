package com.blind.paylock.component;

import com.blind.paylock.datalayer.model.WalletTransaction;
import com.blind.paylock.exception.InsufficientFundsException;
import com.blind.paylock.exception.NotFoundException;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.repository.WalletTransactionRepository;
import com.blind.paylock.utils.enums.WalletTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletProcessorImpl implements WalletProcessor {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository txRepository;

    @Override
    public Mono<Void> debit(
            UUID userId,
            BigDecimal amount,
            String referenceType,
            String referenceId
    ) {
        return walletRepository.findByUserId(userId)
            .switchIfEmpty(Mono.error(
                new NotFoundException("Wallet not found")
            ))
            .flatMap(wallet -> {
                if (wallet.getBalance().compareTo(amount) < 0) {
                    return Mono.error(
                        new InsufficientFundsException("Insufficient wallet balance")
                    );
                }

                wallet.setBalance(wallet.getBalance().subtract(amount));
                wallet.setUpdatedAt(LocalDateTime.now());
                wallet.setNew(false); // Mark as existing entity for UPDATE

                WalletTransaction tx = WalletTransaction.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .type(WalletTransactionType.valueOf("DEBIT"))
                    .amount(amount)
                    .referenceType(referenceType)
                    .referenceId(UUID.fromString(referenceId))
                    .createdAt(LocalDateTime.now())
                    .build();

                return walletRepository.save(wallet)
                    .then(txRepository.save(tx))
                    .then();
            });
    }

    @Override
    public Mono<Void> credit(
            UUID userId,
            BigDecimal amount,
            String referenceType,
            String referenceId
    ) {
        return walletRepository.findByUserId(userId)
            .flatMap(wallet -> {
                wallet.setBalance(wallet.getBalance().add(amount));
                wallet.setUpdatedAt(LocalDateTime.now());
                wallet.setNew(false); // Mark as existing entity for UPDATE

                WalletTransaction tx = WalletTransaction.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .type(WalletTransactionType.valueOf("CREDIT"))
                    .amount(amount)
                    .referenceType(referenceType)
                    .referenceId(UUID.fromString(referenceId))
                    .createdAt(LocalDateTime.now())
                    .build();

                return walletRepository.save(wallet)
                    .then(txRepository.save(tx))
                    .then();
            });
    }
}
