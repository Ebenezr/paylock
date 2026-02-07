package com.blind.paylock.datalayer.models;

import com.blind.paylock.utils.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation {

  @Id
  private UUID id;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Ticket type ID is required")
  private UUID ticketTypeId;

  @Min(value = 1, message = "Quantity must be at least 1")
  private int quantity;

  @NotNull(message = "Total amount is required")
  @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
  private BigDecimal totalAmount;

  @NotNull(message = "Amount paid is required")
  @DecimalMin(value = "0.00", message = "Amount paid cannot be negative")
  private BigDecimal amountPaid;

  @NotNull(message = "Reservation status is required")
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  private LocalDateTime expiryDate;

  @NotNull(message = "Created date is required")
  private LocalDateTime createdAt;
}
