package com.blind.paylock.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class GatewayAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public GatewayAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Authorization header or not Bearer - continuing anonymous: {}", authHeader);
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        log.debug("Found Bearer token of length {} for path {}", token.length(), exchange.getRequest().getPath());

        return jwtUtil.extractUserId(token)
                .zipWith(jwtUtil.extractUserRole(token))
                .flatMap(tuple -> {
                    String userId = tuple.getT1();
                    String role = tuple.getT2();

                    log.debug("JWT parsed successfully - userId={}, role={}", userId, role);

                    List<GrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                })
                .onErrorResume(e -> {
                    log.warn("Failed to parse JWT - allowing anonymous request. Error: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }
}