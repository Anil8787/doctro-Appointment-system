package com.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayCorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200"); // Angular frontend
        config.addAllowedHeader("*");                     // Allow all headers
        config.addAllowedMethod("*");                     // Allow GET, POST, PUT, DELETE
        config.setAllowCredentials(true);                // Needed if sending JWT in headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply to all routes

        return new CorsWebFilter(source);
    }
}
