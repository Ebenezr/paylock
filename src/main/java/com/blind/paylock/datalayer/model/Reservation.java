package com.blind.paylock.datalayer.model;

import com.blind.paylock.utils.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("reservations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation {

  @Id
  private UUID id;

  private UUID userId;
  private UUID ticketTypeId;
  private int quantity;

  private BigDecimal totalAmount;
  private BigDecimal amountPaid;

  private ReservationStatus status;

  private LocalDateTime expiryDate;
  private LocalDateTime createdAt;
}
