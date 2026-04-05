package com.medicine_order_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalPaymentAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        String path = request.getRequestURI();
        String token = request.getHeader("X-Service-Token");

        if (path.startsWith("/api/v1/orders/payment-success")) {

            if (!"payment-service-secret".equals(token)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}