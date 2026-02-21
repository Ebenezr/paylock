package com.blind.paylock.component;

import com.blind.paylock.datalayer.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {


    private static final long EXPIRY_MS = 86400000; // 24h
    private final SecretKey key;

    public JwtUtil(@Value("${app.jwt.secret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User and user.id must be provided to generate a token");
        }

        return Jwts.builder()
                .issuer("paylock-client")
                .claim("iss", "paylock-client") // Add explicit "iss" claim to ensure the issuer appears in the JWT payload (some consumers expect the claim name exactly)
                .subject(user.getId().toString())
                .claim("role", user.getRole() != null ? user.getRole().name() : null)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(key) // use modern overload to avoid deprecated SignatureAlgorithm usage
                .compact();
    }

    public Mono<String> extractUserId(String token) {
        return Mono.fromCallable(() ->
                // Use a parser API compatible with the project's JJWT setup
                Jwts.parser()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    public Mono<String> extractUserRole(String token) {
        return Mono.fromCallable(() ->
                Jwts.parser()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("role")
                        .toString()
        );
    }

}
