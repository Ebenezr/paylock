package com.blind.paylock.datalayer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

  @Id
  private UUID id;

  private UUID reservationId;
  private String qrCode;

  private String status;
  private LocalDateTime issuedAt;
}
