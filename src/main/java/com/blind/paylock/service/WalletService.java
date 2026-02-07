package com.blind.paylock.service;

import com.blind.paylock.datalayer.dto.request.WalletCreditRequestDto;
import com.blind.paylock.datalayer.dto.request.WalletDebitRequestDto;
import com.blind.paylock.utils.apis.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface WalletService {

    Mono<ApiResponse> getWalletBalance(
        Map<String, String> headers
    );

    Mono<ApiResponse> creditWallet(
        WalletCreditRequestDto request,
        Map<String, String> headers
    );

    Mono<ApiResponse> debitWallet(
        WalletDebitRequestDto request,
        Map<String, String> headers
    );
}
