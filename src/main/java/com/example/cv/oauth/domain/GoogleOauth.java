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
public class GoogleOauth implements Oauth {

    @Value("${spring.oauth.google.client-id}")
    private String CLIENT_ID;

    @Value("${spring.oauth.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.oauth.google.scope}")
    private String DATA_ACCESS_SCOPE;

    @Value("${spring.oauth.google.url}")
    private String LOGIN_URL;

    @Value("${spring.oauth.google.callback-url}")
    private String CALLBACK_URL;

    private final ObjectMapper objectMapper;

    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", DATA_ACCESS_SCOPE);
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
        String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("redirect_uri", CALLBACK_URL);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, params, String.class);

        if (HttpStatus.OK == responseEntity.getStatusCode()) {
            return responseEntity;
        }
        return null;
    }

    public ResponseEntity<String> requestUserInfo(GoogleOauthToken oAuthToken) {
        String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        return new RestTemplate().exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
    }

    public GoogleOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        return objectMapper.readValue(response.getBody(), GoogleOauthToken.class);
    }
}
