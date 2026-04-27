package com.shopx.api_gateway.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(
            ServerWebExchange exchange,
            AuthenticationException ex
    ) {

        String message = "Unauthorized";

        // Optional: get custom exception from attributes
        Object attr = exchange.getAttribute("exception");
        if (attr != null) {
            message = attr.toString();
        }

        if (ex!=null && ex.getMessage() != null) {
            message = ex.getMessage();
        }

        String body = """
                {
                  "status": 401,
                  "error": "UNAUTHORIZED",
                  "message": "%s"
                }
                """.formatted(message);

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        var buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}