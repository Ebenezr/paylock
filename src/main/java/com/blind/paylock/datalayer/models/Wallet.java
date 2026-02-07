package com.blind.paylock.datalayer.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {

  @Id
  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Balance is required")
  @DecimalMin(value = "0.00", message = "Balance cannot be negative")
  @Column(nullable = false)
  private BigDecimal balance;

  @NotNull(message = "Updated date is required")
  private LocalDateTime updatedAt;
}
