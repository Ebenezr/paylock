package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletDebitResponseDto {

    private String userId;
    private BigDecimal debitedAmount;
    private BigDecimal newBalance;
}
