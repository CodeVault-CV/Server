package com.example.algoproject.user.service;

import com.example.algoproject.errors.exception.NotExistUserException;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.LoginDto;
import com.example.algoproject.user.dto.TokenDto;
import com.example.algoproject.user.dto.UserInfo;
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
    public LoginDto login(String code) {

        // {code} 를 이용해 Github 에 access_token 요청
        TokenDto tokenDto = accessTokenResponse(code);

        // 받은 access_token 으로 Github 에 사용자 정보 요청
        Map<String, Object> userInfoResponse = userInfoResponse(tokenDto.getAccess_token());

        Optional<User> user = userRepository.findByUserId(userInfoResponse.get("id").toString());

        if (user.isEmpty()) {
            log.info("Add new user to database... " + userInfoResponse.get("login"));
            userRepository.save(new User(userInfoResponse.get("id").toString(), userInfoResponse.get("login").toString(), tokenDto.getAccess_token(), userInfoResponse.get("avatar_url").toString()));
        }
        else {
            log.info(user.get().getName() + " User already exists. Renew User Name & Access Token...");
            // 유저의 이름과 프로필 사진이 변경되었을 수도 있기 때문에 accessToken 과 같이 갱신해 준다
            user.get().update(tokenDto.getAccess_token(), userInfoResponse.get("login").toString(), userInfoResponse.get("avatar_url").toString());
            userRepository.save(user.get());
        }

        return new LoginDto(jwtUtil.makeJWT(userInfoResponse.get("id").toString()), userInfoResponse.get("login").toString());
    }

    @Transactional
    public UserInfo profile(String name) {
        User user = userRepository.findByName(name).orElseThrow(NotExistUserException::new);
        return new UserInfo(user.getName(), user.getImageUrl());
    }

    @Transactional
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(NotExistUserException::new);
    }

    @Transactional
    public User findByName(String name) {
        return userRepository.findByName(name).orElseThrow(NotExistUserException::new);
    }

    //
    // private
    //

    private TokenDto accessTokenResponse(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("User-Agent", "api-test");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TokenDto> response = restTemplate.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                entity,
                TokenDto.class
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
