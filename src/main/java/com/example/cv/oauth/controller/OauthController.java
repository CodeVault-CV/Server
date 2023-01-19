package com.example.cv.oauth.controller;

import com.example.cv.oauth.domain.OauthResponse;
import com.example.cv.oauth.domain.SocialOauth;
import com.example.cv.oauth.service.OauthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OauthController {

    private final OauthService service;

    @Operation(summary = "로그인", description = "각 소셜 로그인 타입을 파라미터로 요청 받아 초기 로그인 과정(인가 코드 받기)을 수행")
    @GetMapping("/{type}")
    public void login(@PathVariable String type) {
        service.request(SocialOauth.valueOf(type.toUpperCase()));
    }

    @Operation(summary = "로그인 콜백 (클라이언트 사용 X)", description = "소셜 로그인에서 인가 코드를 받아 로그인 과정(액세스 토큰 요청, 사용자 정보 요청)을 수행")
    @GetMapping("/{type}/callback")
    public ResponseEntity<Long> callback(@PathVariable String type, @RequestParam String code) {

        // TODO id를 그대로 반환하지 않고 JWT 로 변환하여 반환하는 로직 필요

        OauthResponse response = service.login(SocialOauth.valueOf(type.toUpperCase()), code);
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, response.url())
                .header("user_id", response.id().toString())
                .build();
    }
}
