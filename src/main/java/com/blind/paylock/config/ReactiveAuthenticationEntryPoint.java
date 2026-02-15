package com.blind.paylock.config;

import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ApiResponseHeader;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReactiveAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Object> response = new ApiResponse<>(
                new ApiResponseHeader(
                        ResponseFactory.newRequestRefId(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "UNAUTHORIZED",
                        ex.getMessage() != null ? ex.getMessage() : "Authentication required",
                        LocalDateTime.now()
                ),
                null
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(response);
        } catch (Exception e) {
            bytes = ("{\"header\":{\"responseCode\":401,\"responseMessage\":\"UNAUTHORIZED\"}}")
                    .getBytes();
        }

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
