package com.shopx.api_gateway.config;

import com.shopx.api_gateway.core.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Sameer Shaikh
 * @date 24-04-2026
 * @description
 */


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Gateway service JWT filter invoked.");

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Authentication excluded.");
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        try {

            log.info("JWT token - {}",token);

            if (!jwtUtil.validateToken(token)) {
                return Mono.error(new RuntimeException("Invalid JWT token"));
            }
                Long userId = jwtUtil.extractUserId(token);
                List<String> roles = jwtUtil.extractRoles(token);

                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities
                );

                //exchange.getAttributes().put(Constants.SESSION_USER_ID, userId);

                return chain.filter(exchange)
                        .contextWrite(
                                ReactiveSecurityContextHolder.withAuthentication(auth)
                        );

        } catch (Exception e) {
            log.error("JWT error: {}", e.getMessage());
        }

        return chain.filter(exchange);
    }
}