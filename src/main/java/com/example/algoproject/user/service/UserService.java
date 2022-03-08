package com.example.algoproject.user.service;

import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.TokenResponse;
import com.example.algoproject.user.repository.UserRepository;
import com.example.algoproject.security.JWTUtil;
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
    private final JWTUtil jwtUtil;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Transactional
    public String login(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("User-Agent", "api-test");

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
        ResponseEntity<Map<String, Object>> resp = rt.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });

        Map<String, Object> respBody = resp.getBody();

        Optional<User> user = userRepository.findByUserId(respBody.get("id").toString());

        if (user.isEmpty()) {
            log.info("Add new user to database... " + resp.getBody().get("login"));
            userRepository.save(new User(respBody.get("id").toString(), respBody.get("login").toString(), response.getBody().getAccess_token()));
        }
        else {
            log.info("User already exists. Renew User Name & Access Token...");
            user.get().setAccessToken(response.getBody().getAccess_token());
            user.get().setName(respBody.get("login").toString());
            userRepository.save(user.get());
        }

        // jwt 발급
        String jwtToken = jwtUtil.makeJWT(respBody.get("id").toString());

        return jwtToken;
    }
}
