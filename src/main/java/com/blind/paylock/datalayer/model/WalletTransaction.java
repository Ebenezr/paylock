package com.blind.paylock.datalayer.model;

import com.blind.paylock.utils.enums.WalletTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("wallet_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransaction implements Persistable<UUID> {

  @Id
  private UUID id;

  private UUID userId;

  private WalletTransactionType type;

  private BigDecimal amount;

  private String referenceType;
  private UUID referenceId;

  private LocalDateTime createdAt;

  @Transient
  @Builder.Default
  private boolean isNew = true;

  @Override
  public boolean isNew() {
    return isNew;
  }
}
