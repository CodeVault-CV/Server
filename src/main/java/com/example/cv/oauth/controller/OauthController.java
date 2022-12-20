package com.example.cv.oauth.controller;

import com.example.cv.github.service.GithubService;
import com.example.cv.oauth.service.OauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OauthController {

    private final OauthService oauthService;
    private final GithubService githubService;

    @GetMapping("/login")
    public void login(@RequestParam String code) {
        String accessToken = oauthService.login(code);
    }
}
