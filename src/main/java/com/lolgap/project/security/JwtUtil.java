package com.lolgap.project.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your_secret_key";


    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withIssuer("auth0")
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.require(algorithm)
                .withIssuer("auth0")
                .build()
                .verify(token);
    }

    public String extractUsername(String token) {
        DecodedJWT decodedJWT = validateToken(token);
        return decodedJWT.getSubject();
    }
}
