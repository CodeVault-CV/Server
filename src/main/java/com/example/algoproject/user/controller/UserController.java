package com.example.algoproject.user.controller;

import com.example.algoproject.user.domain.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {
    @Value("${client.id}")
    String clientId;

    @Value("${client.secret}")
    String clientSecret;

    @GetMapping("/api/token")
    public String getToken(@RequestParam String code){


        System.out.println("code : " + code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate rt = new RestTemplate();

        ResponseEntity<TokenResponse> response = rt.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                entity,
                TokenResponse.class
        );

        System.out.println("access_token : " + response.getBody().getAccess_token());

        headers.clear();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + response.getBody().getAccess_token());
        headers.add("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        rt = new RestTemplate();
        ResponseEntity<String> resp = rt.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        System.out.println(resp.toString());

        return resp.getBody();
    }
}