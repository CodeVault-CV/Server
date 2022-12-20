package com.example.cv.oauth.service;

import com.example.cv.oauth.domain.GithubOauth;
import com.example.cv.oauth.domain.GithubTokenDto;
import com.example.cv.oauth.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.cv.oauth.domain.OauthType.*;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final GoogleOauth googleOauth;
    private final KakaoOauth kakaoOauth;
    private final NaverOauth naverOauth;
    private final HttpServletResponse response;

    private final GithubOauth githubOauth;

    public String login(String code) {
        GithubTokenDto oauthToken = githubOauth.getAccessToken(code);
        System.out.println(oauthToken.getAccess_token());

        return oauthToken.getAccess_token();
    }


    public void request(OauthType type) {

        try {
            String url = "";

            if (type == GOOGLE) {
                url = googleOauth.getOauthRedirectURL();
            }
            if (type == KAKAO) {
                url = kakaoOauth.getOauthRedirectURL();
            }
            if (type == NAVER) {
                url = naverOauth.getOauthRedirectURL();
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

        if (type == NAVER) {
            ResponseEntity<String> accessTokenResponse = naverOauth.requestAccessToken(code);
            NaverOauthToken oauthToken = naverOauth.getAccessToken(accessTokenResponse);
            ResponseEntity<String> userInfoResponse = naverOauth.requestUserInfo(oauthToken);
            System.out.println(decode(userInfoResponse.getBody()));
        }
    }

    public static String decode(String uni) {
        StringBuilder str = new StringBuilder();
        for (int i = uni.indexOf("\\u"); i > -1; i = uni.indexOf("\\u")) {
            // euc-kr(%u), utf-8(//u)
            str.append(uni, 0, i);
            str.append((char) Integer.parseInt(uni.substring(i + 2, i + 6), 16));
            uni = uni.substring(i + 6);
        }
        str.append(uni);
        return str.toString();
    }
}
