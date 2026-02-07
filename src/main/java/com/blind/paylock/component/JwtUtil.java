package com.blind.paylock.component;

import com.blind.paylock.datalayer.model.User;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRY_MS = 86400000; // 24h
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateToken(User user) {
        return Jwts.builder()
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

}
