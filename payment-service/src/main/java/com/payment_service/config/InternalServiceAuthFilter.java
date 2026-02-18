package com.payment_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalServiceAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String token = request.getHeader("X-Service-Token");

        // Apply only to internal endpoints
        if (path.startsWith("/product/v1/checkout")) {
            if (!"booking-service-secret".equals(token)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        System.out.println("🔥 InternalServiceAuthFilter PaymentService: path=" + path + ", token=" + token);
        filterChain.doFilter(request, response);
    }
}
