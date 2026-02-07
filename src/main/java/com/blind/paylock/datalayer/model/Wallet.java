package com.blind.paylock.datalayer.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("wallets")
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
  private BigDecimal balance;

  @NotNull(message = "Updated date is required")
  private LocalDateTime updatedAt;

  /** Convenience constructor */
  public Wallet(UUID userId) {
    this.userId = userId;
    this.balance = BigDecimal.ZERO;
    this.updatedAt = LocalDateTime.now();
  }
}
