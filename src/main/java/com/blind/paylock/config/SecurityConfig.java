package com.blind.paylock.config;

import com.blind.paylock.component.JwtAuthenticationFilter;
import com.blind.paylock.component.RateLimitingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
            // Disable CSRF for APIs
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // Add rate limiting filter first
            .addFilterAt(rateLimitingFilter, SecurityWebFiltersOrder.FIRST)

            // Add JWT filter before authentication
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

            // Configure authorization
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/v1/auth/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/events/*/availability").permitAll()
                .anyExchange().authenticated()
            )

            // No login forms
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        return http.build();
    }
}
