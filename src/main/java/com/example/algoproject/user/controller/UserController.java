package com.example.algoproject.user.controller;

import com.example.algoproject.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation(value="로그인", notes="code를 받아 github api 수행후 jwt token 반환")
    @GetMapping("/api/user/login")
    public String login(@RequestParam String code){
        return userService.login(code);
    }
}