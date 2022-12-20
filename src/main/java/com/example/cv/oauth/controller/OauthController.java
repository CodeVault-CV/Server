package com.example.cv.oauth.controller;

import com.example.cv.oauth.domain.OauthType;
import com.example.cv.github.service.GithubService;
import com.example.cv.oauth.service.OauthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService service;
    private final GithubService githubService;

    @GetMapping("/login")
    public void login(@RequestParam String code) {
        String accessToken = oauthService.login(code);
    }

    @GetMapping("/{oauthType}")
    public void redirectLogin(@PathVariable(name = "oauthType") String type) {
        OauthType oauthType = OauthType.valueOf(type.toUpperCase());
        service.request(oauthType);
    }

    @GetMapping("/{oauthType}/callback")
    public void callback(@PathVariable String oauthType, @RequestParam String code) throws JsonProcessingException {
        service.login(OauthType.valueOf(oauthType.toUpperCase()), code);
    }
}
