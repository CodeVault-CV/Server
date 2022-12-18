package com.example.cv.oauth.service;

import com.example.cv.oauth.domain.GoogleOauth;
import com.example.cv.oauth.domain.GoogleOauthToken;
import com.example.cv.oauth.domain.OauthType;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.cv.oauth.domain.OauthType.GOOGLE;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final GoogleOauth googleOauth;
    private final HttpServletResponse response;

    public void request(OauthType type) {

        try {
            String url = "";

            if (type == GOOGLE) {
                url = googleOauth.getOauthRedirectURL();
            }
            response.sendRedirect(url);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void login(OauthType type, String code) throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = googleOauth.requestAccessToken(code);
        GoogleOauthToken oauthToken = googleOauth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = googleOauth.requestUserInfo(oauthToken);

        System.out.println(userInfoResponse.toString());
    }
}
