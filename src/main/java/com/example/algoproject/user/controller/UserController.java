package com.example.algoproject.user.controller;

import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/user/login")
    public String login(@RequestParam String code){
        return userService.login(code);
    }

    // spring security 테스트
    @GetMapping("/test/checkJWT")
    public CustomUserDetailsVO testJWT(@AuthenticationPrincipal CustomUserDetailsVO cudVO) {
        return cudVO;
    }
}