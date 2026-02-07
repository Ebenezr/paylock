package com.blind.paylock.datalayer.models;

import com.blind.paylock.utils.enums.WalletTransactionType;
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
@Table(name = "wallet_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransaction {

  @Id
  private UUID id;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Transaction type is required")
  @Enumerated(EnumType.STRING)
  private WalletTransactionType type;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
  private BigDecimal amount;

  @NotBlank(message = "Reference type is required")
  @Pattern(regexp = "^[a-zA-Z_]+$", message = "Only letters and underscores are allowed for reference type")
  private String referenceType;

  @NotNull(message = "Reference ID is required")
  private UUID referenceId;

  @NotNull(message = "Created date is required")
  private LocalDateTime createdAt;
}
