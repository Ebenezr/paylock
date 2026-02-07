package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.WalletCreditRequestDto;
import com.blind.paylock.datalayer.dto.request.WalletDebitRequestDto;
import com.blind.paylock.datalayer.dto.response.WalletDebitResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface WalletService {

    Mono<ApiResponse<BigDecimal>> getWalletBalance(
    );

    Mono<ApiResponse<Void>> creditWallet(
        WalletCreditRequestDto request
    );

    Mono<ApiResponse<WalletDebitResponseDto>> debitWallet(
        WalletDebitRequestDto request
    );
}
