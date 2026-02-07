package com.blind.paylock.datalayer.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

  @Id
  private UUID id;

  @NotNull(message = "Reservation ID is required")
  private UUID reservationId;

  @NotBlank(message = "QR code is required")
  @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Only letters and numbers are allowed for QR code")
  private String qrCode;

  @NotBlank(message = "Ticket status is required")
  @Pattern(regexp = "^(VALID|USED|CANCELLED|EXPIRED)$", message = "Invalid ticket status")
  private String status;

  @NotNull(message = "Issued date is required")
  private LocalDateTime issuedAt;
}
