package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TicketAvailabilityDto {

    private String ticketTypeId;
    private String name;
    private BigDecimal price;
    private int totalQuantity;
    private int availableQuantity;
}
