package com.blind.paylock.service;

import com.blind.paylock.component.WalletProcessor;
import com.blind.paylock.datalayer.model.Wallet;
import com.blind.paylock.repository.WalletRepository;
import com.blind.paylock.service.impl.WalletServiceImpl;
import com.blind.paylock.datalayer.dto.request.WalletCreditRequestDto;
import com.blind.paylock.datalayer.dto.request.WalletDebitRequestDto;
import com.blind.paylock.datalayer.dto.response.WalletDebitResponseDto;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletProcessor walletProcessor;

    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletServiceImpl(walletRepository, walletProcessor);
    }

    @Test
    void getWalletBalance_whenWalletExists_shouldReturnBalance() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        wallet.setBalance(new BigDecimal("55.50"));
        wallet.setUpdatedAt(LocalDateTime.now());

        when(walletRepository.findByUserId(userId)).thenReturn(Mono.just(wallet));

        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        ApiResponse<BigDecimal> resp = walletService.getWalletBalance()
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                .block();

        assertThat(resp).isNotNull();
        assertThat(resp.getBody()).isEqualTo(new BigDecimal("55.50"));
    }

    @Test
    void creditWallet_shouldCallProcessor_andReturnSuccess() {
        WalletCreditRequestDto req = new WalletCreditRequestDto();
        req.setAmount(new BigDecimal("10"));
        req.setUserId(UUID.randomUUID());

        when(walletProcessor.credit(any(), any(), any(), any())).thenReturn(Mono.empty());

        ApiResponse<Void> r = walletService.creditWallet(req).block();
        assertThat(r).isNotNull();
        assertThat(r.getHeader().getResponseCode()).isEqualTo(200);
    }

    @Test
    void debitWallet_shouldDebit_andReturnResponse() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        wallet.setBalance(new BigDecimal("200.00"));
        wallet.setUpdatedAt(LocalDateTime.now());

        when(walletRepository.findByUserId(userId)).thenReturn(Mono.just(wallet));
        when(walletProcessor.debit(any(), any(), any(), any())).thenReturn(Mono.empty());

        WalletDebitRequestDto req = new WalletDebitRequestDto();
        req.setAmount(new BigDecimal("5"));
        req.setReferenceId("ref-123");

        Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), "x", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        ApiResponse<WalletDebitResponseDto> r = walletService.debitWallet(req)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                .block();

        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getNewBalance()).isEqualTo(new BigDecimal("200.00"));
    }
}

