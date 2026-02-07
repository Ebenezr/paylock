package com.blind.paylock.datalayer.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ticket_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketType {

  @Id
  private UUID id;

  @NotNull(message = "Event ID is required")
  private UUID eventId;

  @NotBlank(message = "Ticket type name is required")
  @Size(min = 2, max = 50, message = "Ticket type name must be between 2 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Only letters, numbers, and spaces are allowed for ticket type name")
  private String name;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.00", message = "Price cannot be negative")
  private BigDecimal price;

  @Min(value = 1, message = "Total quantity must be at least 1")
  private int totalQuantity;
}
