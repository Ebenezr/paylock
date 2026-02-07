package com.blind.paylock.repository;

import com.blind.paylock.datalayer.model.WalletTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface WalletTransactionRepository
        extends ReactiveCrudRepository<WalletTransaction, String> {
}
