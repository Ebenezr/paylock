package com.blind.paylock.datalayer.models;

import com.blind.paylock.utils.enums.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

  @Id
  private UUID id;

  @NotBlank(message = "Event name is required")
  @Size(min = 2, max = 100, message = "Event name must be between 2 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Only letters, numbers, and spaces are allowed for event name")
  private String name;

  @NotNull(message = "Start date is required")
  private LocalDateTime startDate;

  @NotNull(message = "End date is required")
  private LocalDateTime endDate;

  private LocalDateTime paymentCutoff;

  @NotNull(message = "Event status is required")
  @Enumerated(EnumType.STRING)
  private EventStatus status;
}
