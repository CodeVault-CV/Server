package com.example.cv.oauth.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements Oauth {

    @Value("${spring.oauth.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.oauth.kakao.url}")
    private String LOGIN_URL;

    @Value("${spring.oauth.kakao.callback-url}")
    private String CALLBACK_URL;

    private final ObjectMapper mapper;

    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", CLIENT_ID);
        params.put("redirect_uri", CALLBACK_URL);

        // parameter 를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return LOGIN_URL + "?" + parameterString;
    }

    public ResponseEntity<String> requestAccessToken(String code) {
        String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("redirect_uri", CALLBACK_URL);
        params.put("code", code);
        params.put("grant_type", "authorization_code");

        // parameter 를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(KAKAO_TOKEN_REQUEST_URL + "?" + parameterString, String.class);

        if (HttpStatus.OK == responseEntity.getStatusCode()) {
            return responseEntity;
        }
        return null;
    }

    public KakaoOauthToken getAcessToken(ResponseEntity<String> response) throws JsonProcessingException {
        return mapper.readValue(response.getBody(), KakaoOauthToken.class);
    }

    public ResponseEntity<String> requestUserInfo(KakaoOauthToken oAuthToken) {
        String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        return new RestTemplate().exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
    }
}