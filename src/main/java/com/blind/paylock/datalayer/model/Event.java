package com.blind.paylock.datalayer.model;


import com.blind.paylock.utils.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

  @Id
  private UUID id;

  private String name;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime paymentCutoff;

  private EventStatus status;
}
