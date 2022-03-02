package com.example.algoproject.util;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class JWTUtil {

    public String makeJWT(String id) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis()))
                .claim("id", id)
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();
    }

    public String getJWTId(String JWT) {
        return (String) Jwts.parser()
                .setSigningKey("secret")
                .parseClaimsJws(JWT)
                .getBody().get("id").toString();
    }
}