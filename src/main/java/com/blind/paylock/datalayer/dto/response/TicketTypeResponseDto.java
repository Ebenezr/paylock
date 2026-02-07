package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TicketTypeResponseDto {

    private String ticketTypeId;
    private String eventId;

    private String name;
    private BigDecimal price;

    private int totalQuantity;
    private int availableQuantity;
}
