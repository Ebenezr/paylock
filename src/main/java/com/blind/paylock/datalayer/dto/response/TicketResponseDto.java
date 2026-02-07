package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketResponseDto {

    private String ticketId;
    private String qrCode;
    private String status;
}
