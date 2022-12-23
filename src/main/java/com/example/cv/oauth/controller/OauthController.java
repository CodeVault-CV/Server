package com.example.cv.oauth.controller;

import com.example.cv.oauth.domain.OauthType;
import com.example.cv.oauth.service.OauthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService service;

    @Operation(summary = "로그인", description = "각 소셜 로그인 타입을 파라미터로 요청 받아 초기 로그인 과정(인가 코드 받기)을 수행")
    @GetMapping("/{type}")
    public void redirectLogin(@PathVariable String type) {
        service.request(OauthType.valueOf(type.toUpperCase()));
    }

    @Operation(summary = "로그인 콜백 (클라이언트 사용 X)", description = "소셜 로그인에서 인가 코드를 받아 로그인 과정(액세스 토큰 요청, 사용자 정보 요청)을 수행")
    @GetMapping("/{type}/callback")
    public void callback(@PathVariable String type, @RequestParam String code) {
        service.login(OauthType.valueOf(type.toUpperCase()), code);
    }
}
