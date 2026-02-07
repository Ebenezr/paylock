package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RefundResponseDto {

    private String reservationId;
    private BigDecimal refundedAmount;
    private String reason;
}
