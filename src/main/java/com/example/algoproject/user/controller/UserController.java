package com.example.algoproject.user.controller;

import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.dto.LoginResponse;
import com.example.algoproject.user.dto.UserProfileResponse;
import com.example.algoproject.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @ApiOperation(value="로그인", notes="code를 받아 github api 수행후 jwt token 반환")
    @GetMapping("/login")
    public LoginResponse login(@RequestParam String code){
        return userService.login(code);
    }

    @ApiOperation(value="프로필 사진 추가", notes="파일로 사진을 받아 S3에 저장후 이름과 이미지 url을 반환")
    @PostMapping("/images")
    public UserProfileResponse upload(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestParam("images")MultipartFile multipartFile) throws IOException {
        return userService.upload(cudVO, multipartFile);
    }

    @ApiOperation(value="프로필", notes="이름을 파라미터로 받아 이름과 프로필사진 url을 반환")
    @GetMapping("/profile")
    public UserProfileResponse profile(@AuthenticationPrincipal @RequestParam String name) {
        return userService.profile(name);
    }
}