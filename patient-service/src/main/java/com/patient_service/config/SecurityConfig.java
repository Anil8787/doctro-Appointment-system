package com.patient_service.config;

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
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final InternalServiceAuthFilter internalServiceAuthFilter;

    public SecurityConfig(JwtFilter jwtFilter, InternalServiceAuthFilter internalServiceAuthFilter) {
        this.jwtFilter = jwtFilter;
        this.internalServiceAuthFilter = internalServiceAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/v1/patient/getpatientbyid").permitAll() // allow internal calls with token
                        .requestMatchers("/api/v1/patient/**").hasRole("PATIENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(internalServiceAuthFilter, JwtFilter.class); // ✅ add internal filter here

        return http.build();
    }
}
