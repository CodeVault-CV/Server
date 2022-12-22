package com.example.cv.oauth.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Value("${spring.oauth.google.token-url}")
    private String TOKEN_URL;

    @Value("${spring.oauth.google.user-info-url}")
    private String USER_INFO_URL;

    @Override
    public String getOauthRedirectURL() {
        return LOGIN_URL + "?" + paramsToString(Map.of(
                "scope", DATA_ACCESS_SCOPE,
                "response_type", "code",
                "client_id", CLIENT_ID,
                "redirect_uri", CALLBACK_URL));
    }

    @Override
    public OauthToken getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = Map.of(
                "code", code,
                "client_id", CLIENT_ID,
                "client_secret", CLIENT_SECRET,
                "redirect_uri", CALLBACK_URL,
                "grant_type", "authorization_code");

        ResponseEntity<OauthToken> responseEntity = restTemplate.postForEntity(TOKEN_URL, params, OauthToken.class);

        if (HttpStatus.OK != responseEntity.getStatusCode()) {
            throw new RuntimeException("액세스 토큰 요청 실패");
        }
        return responseEntity.getBody();
    }

    @Override
    public String getUserInfo(OauthToken token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token.getAccess_token());

            ResponseEntity<String> userInfoResponse = new RestTemplate()
                    .exchange(USER_INFO_URL, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("사용자 정보 가져오기 실패");
            }

            Map<String, Object> userInfo = new ObjectMapper().readValue(userInfoResponse.getBody(), Map.class);
            return userInfo.get("id").toString();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String paramsToString(Map<String, Object> params) {
        return params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));
    }
}
