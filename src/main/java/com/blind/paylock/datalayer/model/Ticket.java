package com.blind.paylock.datalayer.model;

import com.blind.paylock.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket implements Persistable<UUID> {

  @Id
  private UUID id;

  private UUID reservationId;
  private UUID ticketTypeId;
  private UUID userId;

  private String qrCode;

  @Column("ticket_status")
  private TicketStatus status;

  private LocalDateTime issuedAt;

  @Transient
  private boolean isNew;

  @Override
  public boolean isNew() {
    return isNew;
  }
}
