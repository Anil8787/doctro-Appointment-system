package com.api_gateway.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login"
    );

    private static final Map<String, List<String>> protectedEndpointsWithRoles = new LinkedHashMap<>();

    static {
        protectedEndpointsWithRoles.put( "/api/v1/admin", List.of("ROLE_ADMIN"));
        protectedEndpointsWithRoles.put("/api/v1/auth/change-password", List.of("ROLE_DOCTOR", "ROLE_PATIENT"));
        protectedEndpointsWithRoles.put("/api/v1/doctor/search", List.of("ROLE_PATIENT"));
        protectedEndpointsWithRoles.put("/api/v1/doctor", List.of("ROLE_DOCTOR","ROLE_ADMIN"));
        protectedEndpointsWithRoles.put("/api/v1/patient", List.of("ROLE_PATIENT"));
        protectedEndpointsWithRoles.put("/api/v1/booking", List.of("ROLE_PATIENT"));
        protectedEndpointsWithRoles.put("/product/v1", List.of("ROLE_PATIENT", "ROLE_DOCTOR"));
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestPath = exchange.getRequest().getURI().getPath();
        System.out.println("🔥 Gateway hit: " + requestPath);
        // Allow public endpoints
        if (isPublicEndpoint(requestPath)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtSecret))
                    .build()
                    .verify(token);

            String role = jwt.getClaim("role").asString();

            String userId = jwt.getSubject(); // Auth Service should put userId as subject

            // 🔥 ADD DEBUG LOGS HERE
            System.out.println("🔥 Request Path: " + requestPath);
            System.out.println("🔥 Auth Header: " + authHeader);
            System.out.println("🔥 JWT Role: " + role);
            System.out.println("🔥 Authorized? " + isAuthorized(requestPath, role));

            // Role-based access check
            if (!isAuthorized(requestPath, role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Pass user info downstream to microservices
            exchange = exchange.mutate()
                    .request(r -> r.header("X-User-Id", userId)
                            .header("X-User-Role", role))
                    .build();

        } catch (JWTVerificationException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return openApiEndpoints.stream().anyMatch(path::startsWith);
    }

    private boolean isAuthorized(String path, String role) {
        for (Map.Entry<String, List<String>> entry : protectedEndpointsWithRoles.entrySet()) {
            String protectedPath = entry.getKey();
            List<String> allowedRoles = entry.getValue();

            if (path.startsWith(protectedPath)) {
                return allowedRoles.contains(role);
            }
        }
        return  false; // default deny
    }


    @Override
    public int getOrder() {
        return -1;
    }
}
