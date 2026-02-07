package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.WalletCreditRequestDto;
import com.blind.paylock.datalayer.dto.request.WalletDebitRequestDto;
import com.blind.paylock.datalayer.dto.response.WalletDebitResponseDto;
import com.blind.paylock.service.WalletService;
import com.blind.paylock.utils.apis.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public Mono<ApiResponse<BigDecimal>> getBalance() {
        return walletService.getWalletBalance();
    }

    // Optional: admin/internal only
    @PostMapping("/credit")
    public Mono<ApiResponse<Void>> credit(@RequestBody WalletCreditRequestDto request) {
        return walletService.creditWallet(request);
    }

    @PostMapping("/debit")
    public Mono<ApiResponse<WalletDebitResponseDto>> debit(@RequestBody WalletDebitRequestDto request) {
        return walletService.debitWallet(request);
    }
}
