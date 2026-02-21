-- Paylock schema based on provided table definitions
-- MySQL 8+

CREATE DATABASE IF NOT EXISTS paylock;
USE paylock;

CREATE TABLE IF NOT EXISTS users (
  id CHAR(36) NOT NULL,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at DATETIME NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(30) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS events (
  id CHAR(36) NOT NULL,
  name VARCHAR(255) NOT NULL,
  start_date DATETIME NOT NULL,
  end_date DATETIME NOT NULL,
  payment_cutoff DATETIME NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS wallets (
  user_id CHAR(36) NOT NULL,
  balance DECIMAL(14,2) NOT NULL DEFAULT 0.00,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (user_id),
  CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ticket_types (
  id CHAR(36) NOT NULL,
  event_id CHAR(36) NOT NULL,
  name VARCHAR(100) NOT NULL,
  price DECIMAL(14,2) NOT NULL,
  total_quantity INT NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_ticket_types_event_id (event_id),
  CONSTRAINT fk_ticket_types_event FOREIGN KEY (event_id) REFERENCES events (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reservations (
  id CHAR(36) NOT NULL,
  user_id CHAR(36) NOT NULL,
  ticket_type_id CHAR(36) NOT NULL,
  event_id CHAR(36) NOT NULL,
  quantity INT NOT NULL,
  total_amount DECIMAL(14,2) NOT NULL,
  amount_paid DECIMAL(14,2) NOT NULL DEFAULT 0.00,
  status VARCHAR(30) NOT NULL,
  expiry_date DATETIME NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_reservations_user_id (user_id),
  KEY idx_reservations_status (status),
  KEY idx_reservations_expiry_date (expiry_date),
  CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_reservations_ticket_type FOREIGN KEY (ticket_type_id) REFERENCES ticket_types (id),
  CONSTRAINT fk_reservations_event FOREIGN KEY (event_id) REFERENCES events (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payments (
  id CHAR(36) NOT NULL,
  reservation_id CHAR(36) NOT NULL,
  amount DECIMAL(14,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_payments_reservation_id (reservation_id),
  CONSTRAINT fk_payments_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS refunds (
  id CHAR(36) NOT NULL,
  reservation_id CHAR(36) NOT NULL,
  amount DECIMAL(14,2) NOT NULL,
  reason VARCHAR(30) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_refunds_reservation_id (reservation_id),
  CONSTRAINT fk_refunds_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tickets (
  id CHAR(36) NOT NULL,
  reservation_id CHAR(36) NOT NULL,
  ticket_type_id CHAR(36) NOT NULL,
  user_id CHAR(36) NOT NULL,
  qr_code VARCHAR(255) NOT NULL,
  ticket_status VARCHAR(20) NOT NULL,
  issued_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_tickets_qr_code (qr_code),
  KEY idx_tickets_reservation_id (reservation_id),
  KEY idx_tickets_ticket_type_id (ticket_type_id),
  KEY idx_tickets_user_id (user_id),
  CONSTRAINT fk_tickets_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id),
  CONSTRAINT fk_tickets_ticket_type FOREIGN KEY (ticket_type_id) REFERENCES ticket_types (id),
  CONSTRAINT fk_tickets_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS wallet_transactions (
  id CHAR(36) NOT NULL,
  user_id CHAR(36) NOT NULL,
  type VARCHAR(10) NOT NULL,
  amount DECIMAL(14,2) NOT NULL,
  reference_type VARCHAR(30) NOT NULL,
  reference_id CHAR(36) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_wallet_transactions_user_id (user_id),
  KEY idx_wallet_transactions_reference_type (reference_type),
  CONSTRAINT fk_wallet_transactions_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
