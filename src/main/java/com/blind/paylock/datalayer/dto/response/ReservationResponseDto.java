package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservationResponseDto {

    private String reservationId;
    private String ticketTypeId;
    private int quantity;

    private BigDecimal totalAmount;
    private BigDecimal amountPaid;

    private String status;
    private LocalDateTime expiryDate;
}
