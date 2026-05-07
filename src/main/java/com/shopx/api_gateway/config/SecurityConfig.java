package com.shopx.api_gateway.config;

import com.shopx.api_gateway.handlers.CustomAccessDeniedHandler;
import com.shopx.api_gateway.handlers.CustomAuthEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthEntryPoint authEntryPoint;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("api-gateway filter chain started...");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeExchange(ex -> ex
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/shopx-admin.html",
                                "/webjars/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/users/v3/api-docs",
                                "/products/v3/api-docs",
                                "/orders/v3/api-docs",
                                "/api/v1/auth/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )

                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}