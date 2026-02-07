package com.blind.paylock.datalayer.model;

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

@Table("ticket_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketType implements Persistable<UUID> {

  @Id
  private UUID id;

  private UUID eventId;
  private String name;
  private BigDecimal price;
  private int totalQuantity;
  private LocalDateTime createdAt;

  @Transient
  @Builder.Default
  private boolean isNew = true;

  @Override
  public boolean isNew() {
    return isNew;
  }
}
