package com.example.algoproject.security;

import com.example.algoproject.errors.exception.unauthorized.NotValidateJWTException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String key;

    private final CustomUserDetailsService customUserDetailsService;

    // key Base64로 인코딩
    @PostConstruct
    protected void init() {
        key = Base64.getEncoder().encodeToString(key.getBytes());
    }


    // id로 JWT token 만듦
    public String makeJWT(String id) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(60).toMillis()))
                .claim("id", id)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    // JWT 토큰에서 인증(유저) 정보 조회
    public Authentication getAuthentication(String accessToken) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getJWTId(accessToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // JWT token에서 id 파싱
    public String getJWTId(String JWT) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(JWT)
                .getBody().get("id").toString();
    }

    // header에서 token 값 추출
    public String resolveToken(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token == null || !token.contains("Bearer")) {
            log.info("The header is malformed.");
            throw new NotValidateJWTException();
        }
        String jwt = token.substring("Bearer".length()).stripLeading();
        if (jwt.equals("")) {
            log.info("JWT is null.");
            throw new NotValidateJWTException();
        }
        return jwt;
    }

    // 토큰 유효성 and 만료일자 확인
    public void validateToken(String jwtToken) {

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);
            if (claims.getBody().getExpiration().before(new Date())) { // JWT 만료
                log.info("JWT has expired.");
                throw new NotValidateJWTException();
            }
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) { // JWT 일치 안함
            log.info("JWT do not match.");
            throw new NotValidateJWTException();
        }
    }
}