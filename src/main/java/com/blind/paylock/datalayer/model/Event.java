package com.blind.paylock.datalayer.model;


import com.blind.paylock.utils.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Event implements Persistable<UUID> {

  @Id
  private UUID id;

  private String name;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime paymentCutoff;

  private EventStatus status;

  private LocalDateTime createdAt;

  @Transient
  @Builder.Default
  private boolean isNew = true;

  @Override
  public boolean isNew() {
    return isNew;
  }
}
