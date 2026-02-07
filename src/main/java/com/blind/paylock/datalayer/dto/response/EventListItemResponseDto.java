package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventListItemResponseDto {

    private String eventId;
    private String name;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime paymentCutoff;

    private List<TicketAvailabilityDto> ticketTypes;
}
