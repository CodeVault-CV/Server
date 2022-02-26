package com.example.algoproject.user.service;

import com.example.algoproject.user.dto.TokenResponse;
import com.example.algoproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Transactional
    public String login(String code) {
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
        ResponseEntity<Map<String, String>> resp = rt.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });

        System.out.println(resp.toString());
        System.out.println("name= " + resp.getBody().get("login") + " id= " + resp.getBody().get("id"));

        return resp.getBody().get("id");
    }
}
