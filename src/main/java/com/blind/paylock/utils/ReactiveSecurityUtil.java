package com.blind.paylock.utils;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Utility class for reactive security operations.
 */
public class ReactiveSecurityUtil {

    private ReactiveSecurityUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Get the current authenticated user's ID from the security context.
     *
     * @return Mono containing the user's UUID, or empty if not authenticated
     */
    public static Mono<UUID> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .flatMap(auth -> {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof String userId) {
                        return Mono.just(UUID.fromString(userId));
                    }
                    return Mono.empty();
                });
    }

    /**
     * Get the current authenticated user's ID as a String.
     *
     * @return Mono containing the user's ID as String, or empty if not authenticated
     */
    public static Mono<String> currentUserIdAsString() {
        return currentUserId().map(UUID::toString);
    }
}
