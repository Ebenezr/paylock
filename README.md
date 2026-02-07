# Paylock

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-green)
![License](https://img.shields.io/badge/License-MIT-blue)

A reactive payment and ticketing platform built with Spring Boot WebFlux, designed for handling event reservations, ticket management, and wallet-based payments with high concurrency support.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Key Features](#key-features)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Technology Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4.2 |
| **Reactive Stack** | Spring WebFlux, Project Reactor |
| **Database** | MySQL with R2DBC (Reactive) |
| **Connection Pooling** | R2DBC Pool |
| **Security** | Spring Security (WebFlux) |
| **Validation** | Jakarta Bean Validation |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, Testcontainers |
| **Utilities** | Lombok |

## Project Architecture

Paylock follows a **layered reactive architecture**:

```
┌─────────────────────────────────────────────────────────┐
│                    Controller Layer                      │
│              (REST API endpoints - WebFlux)              │
├─────────────────────────────────────────────────────────┤
│                    Service Layer                         │
│            (Business logic - Reactive Mono/Flux)         │
├─────────────────────────────────────────────────────────┤
│                   Component Layer                        │
│              (Reusable business components)              │
├─────────────────────────────────────────────────────────┤
│                   Repository Layer                       │
│              (R2DBC reactive data access)                │
├─────────────────────────────────────────────────────────┤
│                     Data Layer                           │
│            (MySQL via R2DBC non-blocking driver)         │
└─────────────────────────────────────────────────────────┘
```

### Key Architectural Patterns

- **Reactive Programming**: Fully non-blocking I/O using Project Reactor
- **R2DBC**: Reactive database connectivity for MySQL
- **Component-Based Design**: Reusable business components for common operations
- **Custom Exception Handling**: Global exception handler with specialized exceptions
- **Standardized API Response**: Consistent response format with `ApiResponse<T>` wrapper

## Key Features

### User Management
- User registration and profile management
- User status tracking

### Wallet System
- Digital wallet for each user
- Credit and debit operations
- Balance inquiries
- Transaction history

### Event Management
- Create and manage events
- Publish and cancel events
- List published events with pagination

### Ticket System
- Multiple ticket types per event
- Ticket generation with QR codes
- Ticket validation and invalidation

### Reservation System
- Create reservations with partial payments
- Layaway-style payment plans
- Reservation expiry management
- Automatic cancellation via scheduler

### Payment Processing
- Internal payment processing
- Refund capabilities
- Insufficient funds handling

### Audit & Scheduling
- Audit trail for transactions
- Scheduled tasks for reservation expiry

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **MySQL 8.0+**
- **Docker** (optional, for Testcontainers)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/paylock.git
   cd paylock
   ```

2. **Configure the database**

   Create a MySQL database:
   ```sql
   CREATE DATABASE paylock;
   ```

3. **Update configuration**

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.r2dbc.url=r2dbc:mysql://localhost:3306/paylock?sslMode=DISABLED
   spring.r2dbc.username=your_username
   spring.r2dbc.password=your_password
   ```

4. **Build the project**
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

   The application will start on `http://localhost:8080`

### Running with Docker (Coming Soon)

```bash
docker-compose up -d
```

## Project Structure

```
src/main/java/com/blind/paylock/
├── PaylockApplication.java       # Application entry point
├── component/                    # Reusable business components
│   ├── UserComponent.java
│   └── UserComponentImpl.java
├── config/                       # Configuration classes
│   ├── HeaderProperties.java     # Custom header configuration
│   ├── R2dbcConfig.java          # R2DBC database configuration
│   └── SecurityConfig.java       # Security configuration
├── controller/                   # REST API controllers
│   └── UserController.java
├── datalayer/
│   ├── dto/                      # Data Transfer Objects
│   │   ├── request/              # Request DTOs
│   │   └── response/             # Response DTOs
│   └── model/                    # Domain entities
│       ├── Event.java
│       ├── Payment.java
│       ├── Reservation.java
│       ├── Ticket.java
│       ├── TicketType.java
│       ├── User.java
│       ├── Wallet.java
│       └── WalletTransaction.java
├── exception/                    # Custom exceptions
│   ├── BusinessException.java
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientFundsException.java
│   ├── InvalidStateException.java
│   ├── NotFoundException.java
│   └── ValidationException.java
├── repository/                   # R2DBC repositories
│   ├── UserRepository.java
│   └── WalletRepository.java
├── service/                      # Business logic services
│   ├── AuditService.java
│   ├── EventService.java
│   ├── PaymentService.java
│   ├── RefundService.java
│   ├── ReservationService.java
│   ├── SchedulerService.java
│   ├── TicketService.java
│   ├── TicketTypeService.java
│   ├── UserService.java
│   ├── WalletService.java
│   └── impl/                     # Service implementations
├── utils/
│   ├── apis/                     # API utilities
│   └── enums/                    # Enumerations
└── web/
    └── filter/                   # WebFlux filters
```

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/users` | Register a new user |
| `GET` | `/api/v1/users/me` | Get current user profile |

### Custom Headers

The API uses custom headers for request tracking:

| Header | Description |
|--------|-------------|
| `X-Request-Ref-Id` | Unique request reference ID |
| `X-Organization` | Organization identifier |
| `X-Channel` | Channel identifier |
| `X-User-Id` | User identifier |

## Configuration

### Application Properties

```properties
# Application
spring.application.name=paylock
server.port=8080

# R2DBC MySQL Configuration
spring.r2dbc.url=r2dbc:mysql://localhost:3306/paylock?sslMode=DISABLED
spring.r2dbc.username=root
spring.r2dbc.password=password

# Connection Pool
spring.r2dbc.pool.enabled=true
spring.r2dbc.pool.initial-size=5
spring.r2dbc.pool.max-size=20
spring.r2dbc.pool.max-idle-time=30m

# Logging
logging.level.org.springframework.r2dbc=INFO
logging.level.reactor.netty.http.server=INFO
```

## Testing

The project uses **JUnit 5** and **Testcontainers** for testing:

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

### Test Stack
- **JUnit Jupiter**: Unit and integration testing
- **Testcontainers**: MySQL container for integration tests
- **H2 Database**: In-memory database for unit tests

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Write reactive code using `Mono` and `Flux`
- Include validation annotations on DTOs
- Add comprehensive JavaDoc for public methods
- Use meaningful commit messages

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring WebFlux Guide](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [R2DBC Documentation](https://r2dbc.io/)
- [Project Reactor Reference](https://projectreactor.io/docs/core/release/reference/)

---

**Built with ❤️ using Spring Boot WebFlux**

