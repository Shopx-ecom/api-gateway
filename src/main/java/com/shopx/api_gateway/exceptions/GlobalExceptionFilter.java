package com.shopx.api_gateway.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GlobalExceptionFilter implements WebFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return chain.filter(exchange)
                .onErrorResume(ex -> {
                    log.error("Exception caught in filter: ", ex);

                    var response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    Map<String, Object> error = new HashMap<>();
                    error.put("message", ex.getMessage());
                    error.put("status", 500);

                    try {
                        byte[] bytes = objectMapper.writeValueAsBytes(error);
                        var buffer = response.bufferFactory().wrap(bytes);
                        return response.writeWith(Mono.just(buffer));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}