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
        return Jwts.builder()
                .issuer("paylock-client")
                .claim("iss", "paylock-client") // Add explicit "iss" claim to ensure the issuer appears in the JWT payload (some consumers expect the claim name exactly)
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(key)
                .compact();
    }

    public Mono<String> extractUserId(String token) {
        return Mono.fromCallable(() ->
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    public Mono<String> extractUserRole(String token) {
        return Mono.fromCallable(() ->
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .get("role")
                        .toString()
        );
    }

}
