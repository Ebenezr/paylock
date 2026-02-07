package com.blind.paylock.web.filter;

import com.blind.paylock.config.HeaderProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestContextFilter implements WebFilter {

    private final HeaderProperties headers;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var httpHeaders = exchange.getRequest().getHeaders();

        String headerRefId = httpHeaders.getFirst(headers.getRequestRefId());

        final String requestRefId = (headerRefId == null || headerRefId.isBlank())
                ? UUID.randomUUID().toString()
                : headerRefId;

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("requestRefId", requestRefId));
    }
}
