package com.example.cv.oauth.controller;

import com.example.cv.oauth.domain.OauthType;
import com.example.cv.oauth.service.OauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService service;

    @GetMapping("/{type}")
    public void redirectLogin(@PathVariable String type) {
        service.request(OauthType.valueOf(type.toUpperCase()));
    }

    @GetMapping("/{type}/callback")
    public void callback(@PathVariable String type, @RequestParam String code) {
        service.login(OauthType.valueOf(type.toUpperCase()), code);
    }
}
