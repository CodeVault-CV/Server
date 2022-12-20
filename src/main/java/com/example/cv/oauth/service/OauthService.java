package com.example.cv.oauth.service;

import com.example.cv.oauth.domain.GithubOauth;
import com.example.cv.oauth.domain.GithubTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final GithubOauth githubOauth;

    public String login(String code) {
        GithubTokenDto oauthToken = githubOauth.getAccessToken(code);
        System.out.println(oauthToken.getAccess_token());

        return oauthToken.getAccess_token();
    }
}
