package com.example.algoproject.user.service;

import com.example.algoproject.errors.exception.NotExistUserException;
import com.example.algoproject.s3.S3Uploader;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.dto.LoginResponse;
import com.example.algoproject.user.dto.TokenResponse;
import com.example.algoproject.user.dto.UserProfileResponse;
import com.example.algoproject.user.repository.UserRepository;
import com.example.algoproject.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final S3Uploader s3Uploader;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Transactional
    public LoginResponse login(String code) {

        // {code} 를 이용해 Github 에 access_token 요청
        TokenResponse tokenResponse = accessTokenResponse(code);

        // 받은 access_token 으로 Github 에 사용자 정보 요청
        Map<String, Object> userInfoResponse = userInfoResponse(tokenResponse.getAccess_token());

        Optional<User> user = userRepository.findByUserId(userInfoResponse.get("id").toString());

        if (user.isEmpty()) {
            log.info("Add new user to database... " + userInfoResponse.get("login"));
            userRepository.save(new User(userInfoResponse.get("id").toString(), userInfoResponse.get("login").toString(), tokenResponse.getAccess_token()));
        }
        else {
            log.info(user.get().getName() + " User already exists. Renew User Name & Access Token...");
            user.get().setAccessToken(tokenResponse.getAccess_token());
            // 유저의 이름이 변경되었을 수도 있기 때문에 accessToken 과 같이 갱신해 준다
            user.get().setName(userInfoResponse.get("login").toString());
            userRepository.save(user.get());
        }

        return new LoginResponse(jwtUtil.makeJWT(userInfoResponse.get("id").toString()), userInfoResponse.get("login").toString());
    }

    @Transactional
    public UserProfileResponse upload(CustomUserDetailsVO cudVO, MultipartFile multipartFile) throws IOException {

        String url = s3Uploader.upload(multipartFile, "static");
        User user = userRepository.findByUserId(cudVO.getUsername()).orElseThrow(NotExistUserException::new);

        user.setImageUrl(url);
        userRepository.save(user);

        return new UserProfileResponse(user.getName(), user.getImageUrl());
    }

    @Transactional
    public UserProfileResponse profile(String name) {
        User user = userRepository.findByName(name).orElseThrow(NotExistUserException::new);
        return new UserProfileResponse(user.getName(), user.getImageUrl());
    }

    private TokenResponse accessTokenResponse(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("User-Agent", "api-test");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                entity,
                TokenResponse.class
        );

        return response.getBody();
    }

    private Map<String, Object> userInfoResponse(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + accessToken);
        headers.add("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }
}
