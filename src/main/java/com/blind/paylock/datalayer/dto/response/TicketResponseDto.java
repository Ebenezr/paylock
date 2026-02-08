package com.blind.paylock.datalayer.dto.response;

import com.blind.paylock.utils.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketResponseDto {

    private String ticketId;
    private UUID reservationId;
    private UUID ticketTypeId;
    private UUID userId;

    private String qrCode;
    private TicketStatus status;
    private LocalDateTime issuedAt;
}
