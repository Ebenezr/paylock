package com.blind.paylock.datalayer.model;

import com.blind.paylock.utils.enums.UserRoles;
import com.blind.paylock.utils.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Persistable<UUID> {

  @Id
  private UUID id;

  @NotBlank(message = "User name is required")
  @Size(min = 2, max = 100, message = "User name must be between 2 and 100 characters")
  @Pattern(regexp = "^[a-zA-Z ]+$", message = "Only letters and spaces are allowed for user name")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;


  @NotNull
  private UserStatus status;

  @NotNull(message = "Created date is required")
  private LocalDateTime createdAt;

  @NotNull
  private String password;

  @NotNull
  @Column("role")
  private UserRoles role;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
