package com.blind.paylock.datalayer.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("wallets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet implements Persistable<UUID> {

  @Id
  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Balance is required")
  @DecimalMin(value = "0.00", message = "Balance cannot be negative")
  private BigDecimal balance;

  @NotNull(message = "Updated date is required")
  private LocalDateTime updatedAt;

  @Transient
  @Builder.Default
  private boolean isNew = false;

  /** Convenience constructor */
  public Wallet(UUID userId) {
    this.userId = userId;
    this.balance = BigDecimal.ZERO;
    this.updatedAt = LocalDateTime.now();
    this.isNew = true;
  }

  @Override
  public UUID getId() {
    return userId;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }
}
