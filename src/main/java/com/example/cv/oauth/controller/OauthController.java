package com.example.cv.oauth.controller;

import com.example.cv.oauth.domain.Oauth;
import com.example.cv.oauth.domain.OauthInformation;
import com.example.cv.oauth.domain.OauthInformationDTO;
import com.example.cv.oauth.service.OauthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OauthController {

    private final OauthService service;

    @Operation(summary = "로그인", description = "각 소셜 로그인 타입을 파라미터로 요청 받아 초기 로그인 과정(인가 코드 받기)을 수행")
    @GetMapping("/{type}")
    public void redirectLogin(@PathVariable String type) {
        service.request(Oauth.valueOf(type.toUpperCase()));
    }

    @Operation(summary = "로그인 콜백 (클라이언트 사용 X)", description = "소셜 로그인에서 인가 코드를 받아 로그인 과정(액세스 토큰 요청, 사용자 정보 요청)을 수행")
    @GetMapping("/{type}/callback")
    public ResponseEntity<?> callback(@PathVariable String type, @RequestParam String code) {
        OauthInformation info = service.login(Oauth.valueOf(type.toUpperCase()), code);
        if (info == null) {
            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .location(URI.create("http://localhost:3000/sign_in"))
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new OauthInformationDTO(info.getId(), info.getAccess_token()));
    }
}
