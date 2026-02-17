package com.auth_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret_key;
    @Value("${jwt.expiration_time}")
    private Long expiration_time;

    public String generateToken(String email,String role) {
        return JWT.create()
                .withClaim("role",role)
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+expiration_time))
                .sign(Algorithm.HMAC256(secret_key));
    }

//    public String validateTokenAndGetSubject(String token) {
//        return JWT.require(Algorithm.HMAC256(secret_key))
//                .build()
//                .verify(token)
//                .getSubject();
//    }
}
