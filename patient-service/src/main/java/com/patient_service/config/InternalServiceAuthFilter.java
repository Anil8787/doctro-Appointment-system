package com.patient_service.config;

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

        if (path.equals("/api/v1/patient/getpatientbyid")) {
            // Only block if token exists but is invalid
            if (token != null && !"booking-service-secret".equals(token)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            // ✅ If token is null, we allow it and JwtFilter handles authentication
        }

        System.out.println("🔥 InternalServiceAuthFilter: path=" + path + ", token=" + token);

        filterChain.doFilter(request, response);
    }
}
