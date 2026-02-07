package com.blind.paylock.datalayer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponseDto {

    private String eventId;
    private String name;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime paymentCutoff;

    private String status; // DRAFT, PUBLISHED, CLOSED, CANCELED
}
