package com.blind.paylock.repository;


import com.blind.paylock.datalayer.model.Wallet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface WalletRepository extends ReactiveCrudRepository<Wallet, UUID> {
}
