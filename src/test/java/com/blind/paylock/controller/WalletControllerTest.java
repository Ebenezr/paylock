package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.WalletCreditRequestDto;
import com.blind.paylock.datalayer.dto.request.WalletDebitRequestDto;
import com.blind.paylock.datalayer.dto.response.WalletDebitResponseDto;
import com.blind.paylock.service.WalletService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    private WalletController walletController;

    @BeforeEach
    void setUp() {
        walletController = new WalletController(walletService);
    }

    @Test
    void getBalance_shouldReturnBalanceFromService() {
        BigDecimal expected = new BigDecimal("123.45");
        when(walletService.getWalletBalance()).thenReturn(ResponseFactory.success(expected, "ref-1"));

        ApiResponse<BigDecimal> response = walletController.getBalance().block();
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void credit_shouldCallService_andReturnVoidResponse() {
        WalletCreditRequestDto req = new WalletCreditRequestDto();
        req.setAmount(new BigDecimal("10"));
        req.setUserId(UUID.randomUUID());

        when(walletService.creditWallet(any())).thenReturn(ResponseFactory.success(null, "ref-2"));

        ApiResponse<Void> response = walletController.credit(req).block();
        assertThat(response).isNotNull();
        assertThat(response.getHeader()).isNotNull();
        assertThat(response.getHeader().getResponseCode()).isEqualTo(200);
    }

    @Test
    void debit_shouldReturnDebitResponse() {
        WalletDebitRequestDto req = new WalletDebitRequestDto();
        req.setAmount(new BigDecimal("5"));
        // WalletDebitRequestDto expects a referenceId (String)
        String refId = UUID.randomUUID().toString();
        req.setReferenceId(refId);

        WalletDebitResponseDto debitResp = WalletDebitResponseDto.builder()
                .userId("user-1")
                .debitedAmount(new BigDecimal("5"))
                .newBalance(new BigDecimal("100.00"))
                .build();

        when(walletService.debitWallet(any())).thenReturn(ResponseFactory.success(debitResp, "ref-3"));

        ApiResponse<WalletDebitResponseDto> response = walletController.debit(req).block();
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNewBalance()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.getBody().getDebitedAmount()).isEqualTo(new BigDecimal("5"));
    }
}
