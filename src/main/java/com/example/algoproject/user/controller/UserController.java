package com.example.algoproject.user.controller;

import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary="로그인", description="code를 받아 github api 수행후 jwt token 반환")
    @GetMapping("/login")
    public CommonResponse login(@RequestParam String code){
        return userService.login(code);
    }

    @Operation(summary="프로필", description="이름을 파라미터로 받아 이름과 프로필사진 url을 반환")
    @GetMapping("/profile")
    public CommonResponse profile(@AuthenticationPrincipal @RequestParam String name) {
        return userService.profile(name);
    }
}