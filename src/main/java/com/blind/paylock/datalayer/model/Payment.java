package com.blind.paylock.datalayer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

  @Id
  private UUID id;

  private UUID reservationId;
  private BigDecimal amount;

  private String status;
  private LocalDateTime createdAt;
}
