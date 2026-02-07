package com.blind.paylock.datalayer.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

  @Id
  private UUID id;

  @NotNull(message = "Reservation ID is required")
  private UUID reservationId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
  private BigDecimal amount;

  @NotBlank(message = "Payment status is required")
  @Pattern(regexp = "^(PENDING|COMPLETED|FAILED|REFUNDED)$", message = "Invalid payment status")
  private String status;

  @NotNull(message = "Created date is required")
  private LocalDateTime createdAt;
}
