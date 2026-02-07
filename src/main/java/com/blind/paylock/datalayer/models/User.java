package com.blind.paylock.datalayer.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  @Id
  private UUID id;

  @NotBlank(message = "User name is required")
  @Size(min = 2, max = 100, message = "User name must be between 2 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z ]+$", message = "Only letters and spaces are allowed for user name")
  @Column(nullable = false)
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Column(nullable = false, unique = true)
  private String email;

  @NotNull(message = "Created date is required")
  private LocalDateTime createdAt;
}
