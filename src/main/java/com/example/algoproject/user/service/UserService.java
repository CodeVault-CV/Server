package com.example.algoproject.user.service;

import com.example.algoproject.errors.exception.unauthorized.FailedResponseException;
import com.example.algoproject.errors.exception.notfound.NotExistUserException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.LoginDto;
import com.example.algoproject.user.dto.TokenDto;
import com.example.algoproject.user.repository.UserRepository;
import com.example.algoproject.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ResponseService responseService;
    private final JWTUtil jwtUtil;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Transactional
    public CommonResponse login(String code) {

        // {code} 를 이용해 Github 에 access_token 요청
        String token = accessTokenResponse(code);

        // 받은 access_token 으로 Github 에 사용자 정보 요청
        Map<String, Object> userInfoResponse = userInfoResponse(token);

        Optional<User> user = userRepository.findById(userInfoResponse.get("id").toString());

        if (user.isEmpty()) {
            // 새로운 유저를 데이터베이스에 추가
            log.info("Add new user to database... " + userInfoResponse.get("login"));
            userRepository.save(new User(userInfoResponse.get("id").toString(), userInfoResponse.get("login").toString(), token, userInfoResponse.get("avatar_url").toString()));
        }
        else {
            log.info(user.get().getName() + " User already exists. Renew User Name & Access Token...");
            // 유저의 이름과 프로필 사진이 변경되었을 수도 있기 때문에 accessToken 과 같이 갱신해 준다
            user.get().update(token, userInfoResponse.get("login").toString(), userInfoResponse.get("avatar_url").toString());
            userRepository.save(user.get());
        }
        return responseService.getSingleResponse(new LoginDto(jwtUtil.makeJWT(userInfoResponse.get("id").toString()), userInfoResponse.get("login").toString()));
    }

    @Transactional
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(NotExistUserException::new);
    }

    @Transactional
    public User findByName(String name) {
        return userRepository.findByName(name).orElseThrow(NotExistUserException::new);
    }

    @Transactional
    public List<User> findByNameContains(String name) {
        return userRepository.findByNameContains(name);
    }

    //
    // private
    //

    private String accessTokenResponse(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("User-Agent", "api-test");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TokenDto> response = restTemplate.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                entity,
                TokenDto.class
        );

        // code가 유효하지 않을 때
        if(response.getBody().getAccess_token() == null)
            throw new FailedResponseException("code가 유효하지 않거나 파기되었습니다");

        return response.getBody().getAccess_token();
    }

    private Map<String, Object> userInfoResponse(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + token);
        headers.add("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>(){});
            return response.getBody();
        } catch (RuntimeException ex) { // accessToken 이 유효하지 않을 때는 code에서 제대로 왔다면 거의 발생하지 않음
            throw new FailedResponseException("유효하지 않은 Access Token 입니다");
        }
    }
}
