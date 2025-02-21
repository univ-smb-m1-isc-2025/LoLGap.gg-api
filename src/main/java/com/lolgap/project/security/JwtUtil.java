package com.lolgap.project.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil
{
    private static final String SECRET_KEY = "your_secret_key";
    private static final String ISSUER = "lolgap";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    public String generateToken(String username)
    {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .sign(ALGORITHM);
    }

    public DecodedJWT validateToken(String token)
    {
        return JWT.require(ALGORITHM)
                .withIssuer(ISSUER)
                .build()
                .verify(token);
    }

    public String extractUsername(String token)
    {
        return validateToken(token).getSubject();
    }
}
