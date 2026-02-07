package com.blind.paylock.service.impl;

import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.datalayer.dto.request.WalletCreditRequestDto;
import com.blind.paylock.datalayer.dto.request.WalletDebitRequestDto;
import com.blind.paylock.datalayer.dto.response.WalletDebitResponseDto;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.WalletService;
import com.blind.paylock.utils.ReactiveSecurityUtil;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletProcessor walletProcessor;

    @Override
    public Mono<ApiResponse<BigDecimal>> getWalletBalance() {
        String requestRefId = ResponseFactory.newRequestRefId();

        return ReactiveSecurityUtil.currentUserId()
            .flatMap(userId ->
                walletRepository.findByUserId(userId)
                    .flatMap(wallet ->
                        ResponseFactory.success(wallet.getBalance(), requestRefId)
                    )
            )
            .switchIfEmpty(
                ResponseFactory.errorMono(
                    HttpStatus.NOT_FOUND,
                    "WALLET_NOT_FOUND",
                    "Wallet not found",
                    requestRefId
                )
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
            .then(ResponseFactory.success(null, requestRefId));
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
                        .flatMap(wallet ->
                            ResponseFactory.success(
                                WalletDebitResponseDto.builder()
                                    .userId(userId.toString())
                                    .debitedAmount(request.getAmount())
                                    .newBalance(wallet.getBalance())
                                    .build(),
                                requestRefId
                            )
                        )
                )
            );
    }
}
