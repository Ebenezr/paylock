package com.blind.paylock.config;

import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ApiResponseHeader;
import com.blind.paylock.utils.apis.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReactiveAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Object> response = new ApiResponse<>(
                new ApiResponseHeader(
                        ResponseFactory.newRequestRefId(),
                        HttpStatus.FORBIDDEN.value(),
                        "ACCESS_DENIED",
                        denied.getMessage() != null ? denied.getMessage() : "Access Denied",
                        LocalDateTime.now()
                ),
                null
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(response);
        } catch (Exception e) {
            bytes = ("{\"header\":{\"responseCode\":403,\"responseMessage\":\"ACCESS_DENIED\"}}")
                    .getBytes();
        }

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}

