package com.booking_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Allows @PreAuthorize annotations
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final InternalPaymentAuthFilter internalPaymentAuthFilter;

    public SecurityConfig(JwtFilter jwtFilter, InternalPaymentAuthFilter internalPaymentAuthFilter) {
        this.jwtFilter = jwtFilter;
        this.internalPaymentAuthFilter = internalPaymentAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers("/api/v1/booking/confirm").permitAll()

                        // Only patients can create & view bookings
                        .requestMatchers("/api/v1/booking/**").hasRole("PATIENT")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalPaymentAuthFilter, JwtFilter.class);

        return http.build();
    }
}
