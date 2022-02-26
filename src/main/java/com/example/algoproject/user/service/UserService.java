package com.example.algoproject.user.service;

import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.TokenResponse;
import com.example.algoproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@Slf4j
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

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate rt = new RestTemplate();

        // {code} 를 이용해 Github 에 access_token 요청
        ResponseEntity<TokenResponse> response = rt.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                entity,
                TokenResponse.class
        );

        headers.clear();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + response.getBody().getAccess_token());
        headers.add("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        rt = new RestTemplate();

        // 받은 access_token 으로 사용자 정보 요청
        ResponseEntity<Map<String, String>> resp = rt.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });

        Map<String, String> respBody = resp.getBody();

        Optional<User> user = userRepository.findByUserId(respBody.get("id"));

        if (user.isEmpty()) {
            log.info("Add new user to database... " + resp.getBody().get("login"));
            userRepository.save(new User(respBody.get("id"), respBody.get("login"), response.getBody().getAccess_token()));
        }
        else {
            log.info("User already exists. Renew User Name & Access Token...");
            user.get().setAccessToken(response.getBody().getAccess_token());
            user.get().setName(respBody.get("login"));
            userRepository.save(user.get());
        }

        return resp.getBody().get("id");
    }
}
