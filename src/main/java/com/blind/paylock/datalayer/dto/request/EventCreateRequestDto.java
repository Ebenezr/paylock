package com.blind.paylock.datalayer.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreateRequestDto {

    @NotBlank
    private String name;

    @NotNull
    @Future
    private LocalDateTime startDate;

    @NotNull
    @Future
    private LocalDateTime endDate;

    @NotNull
    @Future
    private LocalDateTime paymentCutoff;
}
