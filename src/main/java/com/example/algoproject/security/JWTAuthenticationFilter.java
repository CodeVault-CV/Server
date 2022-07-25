package com.example.algoproject.security;

import com.example.algoproject.errors.exception.unauthorized.NotValidateJWTException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            // 헤더에서 jwtToken 분리
            String token = jwtUtil.resolveToken(request);

            // 유효한 토큰인지 확인
            jwtUtil.validateToken(token);
            // 토큰으로부터 유저 정보를 받아옴
            Authentication authentication = jwtUtil.getAuthentication(token);
            // SecurityContext 에 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (NotValidateJWTException ex) {
            /* Filter 단 Custom 에러 (JWT 관련 에러) 핸들링 */
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("code", HttpStatus.UNAUTHORIZED.value());
            errorDetails.put("message", ex.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(response.getWriter(), errorDetails);
        }
    }
}
