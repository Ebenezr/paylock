# Build stage - Maven with JDK 21
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom and Maven wrapper
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies first (cached layer)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -DskipTests

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage - lightweight alpine
FROM amazoncorretto:21-alpine

WORKDIR /app

# Set timezone
ENV TZ=Africa/Nairobi
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Copy compiled JAR from builder stage
COPY --from=builder /app/target/paylock-0.0.1-SNAPSHOT.jar paylock.jar

# Spring Boot temp volume
VOLUME /tmp

EXPOSE 8080

# Health check
HEALTHCHECK --interval=15s --timeout=5s --retries=3 --start-period=40s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-Xmx256m", "-XX:+UseG1GC", "-Djava.security.egd=file:/dev/./urandom", "-jar", "paylock.jar"]
