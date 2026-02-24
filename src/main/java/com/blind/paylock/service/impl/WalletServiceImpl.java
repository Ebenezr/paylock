package com.blind.paylock.service.impl;

import com.blind.paylock.component.PaylockLogManager;
import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.datalayer.dto.request.WalletCreditRequestDto;
import com.blind.paylock.datalayer.dto.request.WalletDebitRequestDto;
import com.blind.paylock.datalayer.dto.response.WalletDebitResponseDto;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.WalletService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletProcessor walletProcessor;

    LocalDateTime startTime = LocalDateTime.now();

    public WalletServiceImpl(WalletRepository walletRepository, WalletProcessor walletProcessor) {
        this.walletRepository = walletRepository;
        this.walletProcessor = walletProcessor;
    }

    @Override
    public Mono<ApiResponse<BigDecimal>> getWalletBalance() {
        String requestRefId = ResponseFactory.newRequestRefId();


        return ReactiveSecurityUtil.currentUserId()
            .flatMap(userId ->
                walletRepository.findByUserId(userId)
                    .flatMap(wallet -> {
                        PaylockLogManager.info(
                            requestRefId,
                            "GET_WALLET_BALANCE",
                            PaylockLogManager.processDuration(startTime),
                            "WALLET_BALANCE_FETCHED"
                        );
                        return ResponseFactory.success(wallet.getBalance(), requestRefId);
                    })
            )
            .switchIfEmpty(
                Mono.defer(() -> {
                    PaylockLogManager.error(
                        requestRefId,
                        "GET_WALLET_BALANCE_ERROR",
                        PaylockLogManager.processDuration(startTime),
                        "WALLET_NOT_FOUND_OR_UNAUTHORIZED"
                    );
                    return ResponseFactory.errorMono(
                        HttpStatus.NOT_FOUND,
                        "WALLET_NOT_FOUND",
                        "Wallet not found",
                        requestRefId
                    );
                })
            );
    }

    @Override
    public Mono<ApiResponse<Void>> creditWallet(WalletCreditRequestDto request) {
        String requestRefId = ResponseFactory.newRequestRefId();


        return walletProcessor.credit(
                request.getUserId(),
                request.getAmount(),
                "ADMIN_CREDIT",
                requestRefId
            )
            .then(ResponseFactory.<Void>success(null, requestRefId))
            .doOnSuccess(ignored -> PaylockLogManager.info(
                requestRefId,
                "CREDIT_WALLET",
                PaylockLogManager.processDuration(startTime),
                "WALLET_CREDITED"
            ))
            .doOnError(err -> PaylockLogManager.error(
                requestRefId,
                "CREDIT_WALLET_ERROR",
                PaylockLogManager.processDuration(startTime),
                err.getMessage()
            ));
    }

    @Override
    public Mono<ApiResponse<WalletDebitResponseDto>> debitWallet(WalletDebitRequestDto request) {
        String requestRefId = ResponseFactory.newRequestRefId();


        return ReactiveSecurityUtil.currentUserId()
            .flatMap(userId ->
                walletProcessor.debit(
                        userId,
                        request.getAmount(),
                        "RESERVATION",
                        request.getReferenceId()
                )
                .then(
                    walletRepository.findByUserId(userId)
                        .flatMap(wallet -> {
                            PaylockLogManager.info(
                                requestRefId,
                                "DEBIT_WALLET",
                                PaylockLogManager.processDuration(startTime),
                                "WALLET_DEBITED"
                            );
                            return ResponseFactory.success(
                                WalletDebitResponseDto.builder()
                                    .userId(userId.toString())
                                    .debitedAmount(request.getAmount())
                                    .newBalance(wallet.getBalance())
                                    .build(),
                                requestRefId
                            );
                        })
                )
            )
            .switchIfEmpty(Mono.defer(() -> {
                PaylockLogManager.error(
                    requestRefId,
                    "DEBIT_WALLET_ERROR",
                    PaylockLogManager.processDuration(startTime),
                    "UNAUTHORIZED"
                );
                return ResponseFactory.errorMono(
                    HttpStatus.UNAUTHORIZED,
                    "UNAUTHORIZED",
                    "User not authenticated",
                    requestRefId
                );
            }));
    }
}
