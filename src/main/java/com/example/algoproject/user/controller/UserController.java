package com.example.algoproject.user.controller;

import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary="로그인", description="code름(인증 코드)를 받아 token(JWT Token), name(사용자 이름)을 반환")
    @GetMapping("/login")
    public CommonResponse login(@RequestParam String code){
        return userService.login(code);
    }
}