package com.blind.paylock.datalayer.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletCreditRequestDto {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String userId;
}
