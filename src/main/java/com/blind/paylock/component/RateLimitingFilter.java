package com.blind.paylock.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class RateLimitingFilter implements WebFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);

    // Map of session/IP to request timestamps
    private final Map<String, Queue<Instant>> requestCounts = new ConcurrentHashMap<>();

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        // Only apply rate limiting to POST requests
        if (exchange.getRequest().getMethod() != HttpMethod.POST) {
            return chain.filter(exchange);
        }

        String clientId = getClientIp(exchange);

        if (isRateLimited(clientId)) {
            log.warn("Rate limit exceeded for client: {}", clientId);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
            exchange.getResponse().getHeaders().add("Retry-After", "60");
            return exchange.getResponse().setComplete();
        }

        recordRequest(clientId);

        // Add rate limit headers to response
        int remaining = MAX_REQUESTS_PER_MINUTE - getRequestCount(clientId);
        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));

        return chain.filter(exchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        // Check for X-Forwarded-For header (for proxied requests)
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }

        // Fall back to remote address
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress != null) {
            return remoteAddress.getAddress().getHostAddress();
        }

        return "unknown";
    }

    private boolean isRateLimited(String clientId) {
        cleanupOldRequests(clientId);
        return getRequestCount(clientId) >= MAX_REQUESTS_PER_MINUTE;
    }

    private void recordRequest(String clientId) {
        requestCounts.computeIfAbsent(clientId, k -> new ConcurrentLinkedQueue<>())
                .add(Instant.now());
    }

    private int getRequestCount(String clientId) {
        Queue<Instant> timestamps = requestCounts.get(clientId);
        return timestamps == null ? 0 : timestamps.size();
    }

    private void cleanupOldRequests(String clientId) {
        Queue<Instant> timestamps = requestCounts.get(clientId);
        if (timestamps == null) {
            return;
        }

        Instant cutoff = Instant.now().minus(WINDOW_DURATION);
        timestamps.removeIf(timestamp -> timestamp.isBefore(cutoff));

        // Remove entry if no requests remain
        if (timestamps.isEmpty()) {
            requestCounts.remove(clientId);
        }
    }
}


