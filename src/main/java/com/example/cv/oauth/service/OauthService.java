package com.example.cv.oauth.service;

import com.example.cv.oauth.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.cv.oauth.domain.OauthType.GOOGLE;
import static com.example.cv.oauth.domain.OauthType.KAKAO;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final GoogleOauth googleOauth;
    private final KakaoOauth kakaoOauth;
    private final HttpServletResponse response;

    public void request(OauthType type) {

        try {
            String url = "";

            if (type == GOOGLE) {
                url = googleOauth.getOauthRedirectURL();
            }
            if (type == KAKAO) {
                url = kakaoOauth.getOauthRedirectURL();
            }
            response.sendRedirect(url);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void login(OauthType type, String code) throws JsonProcessingException {
        if (type == GOOGLE) {
            ResponseEntity<String> accessTokenResponse = googleOauth.requestAccessToken(code);
            GoogleOauthToken oauthToken = googleOauth.getAccessToken(accessTokenResponse);
            ResponseEntity<String> userInfoResponse = googleOauth.requestUserInfo(oauthToken);
            System.out.println(userInfoResponse.getBody());
        }

        if (type == KAKAO) {
            ResponseEntity<String> accessTokenResponse = kakaoOauth.requestAccessToken(code);
            KakaoOauthToken oauthToken = kakaoOauth.getAcessToken(accessTokenResponse);
            ResponseEntity<String> userInfoResponse = kakaoOauth.requestUserInfo(oauthToken);
            System.out.println(userInfoResponse.getBody());
        }
    }
}
