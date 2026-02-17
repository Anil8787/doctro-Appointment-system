package com.auth_service.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public DecodedJWT decode(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token);
    }

    public String extractEmail(String token) {
        return decode(token).getSubject();
    }

    public String extractRole(String token) {
        return decode(token).getClaim("role").asString();
    }
}
