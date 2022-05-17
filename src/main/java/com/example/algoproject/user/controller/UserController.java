package com.example.algoproject.user.controller;

import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.errors.response.SingleResponse;
import com.example.algoproject.user.dto.LoginDto;
import com.example.algoproject.user.dto.UserInfo;
import com.example.algoproject.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;

    @ApiOperation(value="로그인", notes="code를 받아 github api 수행후 jwt token 반환")
    @GetMapping("/login")
    public SingleResponse<LoginDto> login(@RequestParam String code){
        return responseService.getSingleResponse(userService.login(code));
    }

    @ApiOperation(value="프로필", notes="이름을 파라미터로 받아 이름과 프로필사진 url을 반환")
    @GetMapping("/profile")
    public SingleResponse<UserInfo> profile(@AuthenticationPrincipal @RequestParam String name) {
        return responseService.getSingleResponse(userService.profile(name));
    }
}