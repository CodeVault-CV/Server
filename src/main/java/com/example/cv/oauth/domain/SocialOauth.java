package com.example.cv.oauth.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum SocialOauth {
    GOOGLE {
        @Override
        public String getRedirectURL() {
            return getUrl(Information.google);
        }

        @Override
        public OauthToken getAccessToken(String code) {
            return SocialOauth.getTokenByPost(code, Information.google);
        }

        @Override
        public String getUserInformation(OauthToken token) {
            try {
                Map<String, Object> userInfo = new ObjectMapper().readValue(getInformation(token, Information.google).getBody(), Map.class);
                return userInfo.get("id").toString();

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    },
    KAKAO {
        @Override
        public String getRedirectURL() {
            return getUrl(Information.kakao);
        }

        @Override
        public OauthToken getAccessToken(String code) {
            return SocialOauth.getTokenByGet(code, Information.kakao);
        }

        @Override
        public String getUserInformation(OauthToken token) {
            try {
                Map<String, Object> userInfo = new ObjectMapper().readValue(getInformation(token, Information.kakao).getBody(), Map.class);
                return userInfo.get("id").toString();

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    },
    NAVER {
        @Override
        public String getRedirectURL() {
            return getUrl(Information.naver);
        }

        @Override
        public OauthToken getAccessToken(String code) {
            return SocialOauth.getTokenByGet(code, Information.naver);
        }

        @Override
        public String getUserInformation(OauthToken token) {
            try {
                Map<String, Map<String, String>> userInfo = new ObjectMapper().readValue(getInformation(token, Information.naver).getBody(), Map.class);
                return userInfo.get("response").get("id");

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    },
    GITHUB {
        @Override
        public String getRedirectURL() {
            return getUrl(Information.github);
        }

        @Override
        public OauthToken getAccessToken(String code) {
            return SocialOauth.getTokenByPost(code, Information.github);
        }

        @Override
        public String getUserInformation(OauthToken token) {
            try {
                Map<String, Object> userInfo = new ObjectMapper().readValue(getInformation(token, Information.github).getBody(), Map.class);
                return userInfo.get("id").toString();

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public abstract String getRedirectURL();

    public abstract OauthToken getAccessToken(String code);

    public abstract String getUserInformation(OauthToken token);

    private static String paramsToString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    private static String getUrl(Map<String, Map<String, String>> info) {
        return info.get("urls").get("login") + "?" + paramsToString(info.get("redirect_params"));
    }

    private static OauthToken getTokenByPost(String code, Map<String, Map<String, String>> info) {
        Map<String, String> parameters = new HashMap<>(info.get("token_params"));
        parameters.put("code", code);

        ResponseEntity<OauthToken> responseEntity = postResponseEntity(parameters, info.get("urls").get("token"));

        if (HttpStatus.OK != responseEntity.getStatusCode()) {
            throw new RuntimeException("액세스 토큰 요청 실패");
        }
        return responseEntity.getBody();
    }

    private static OauthToken getTokenByGet(String code, Map<String, Map<String, String>> info) {
        Map<String, String> parameters = new HashMap<>(info.get("token_params"));
        parameters.put("code", code);

        ResponseEntity<OauthToken> responseEntity = getResponseEntity(parameters, info.get("urls").get("token"));

        if (HttpStatus.OK != responseEntity.getStatusCode()) {
            throw new RuntimeException("액세스 토큰 요청 실패");
        }
        return responseEntity.getBody();
    }

    private static ResponseEntity<OauthToken> postResponseEntity(Map<String, String> parameters, String url) {
        return new RestTemplate().postForEntity(url, parameters, OauthToken.class);
    }

    private static ResponseEntity<OauthToken> getResponseEntity(Map<String, String> parameters, String url) {
        return new RestTemplate().getForEntity(url + "?" + paramsToString(parameters), OauthToken.class);
    }

    private static ResponseEntity<String> getInformation(OauthToken token, Map<String, Map<String, String>> info) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getAccess_token());

        ResponseEntity<String> userInfoResponse = new RestTemplate().exchange(
                info.get("urls").get("info"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("사용자 정보 가져오기 실패");
        }
        return userInfoResponse;
    }
}
