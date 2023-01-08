package com.example.cv.oauth.service;

import com.example.cv.oauth.domain.Oauth;
import com.example.cv.oauth.domain.OauthId;
import com.example.cv.oauth.domain.OauthInformation;
import com.example.cv.oauth.domain.OauthToken;
import com.example.cv.oauth.repository.OauthInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final OauthInformationRepository repository;
    private final HttpServletResponse response;

    public void request(Oauth type) {
        try {
            response.sendRedirect(type.getRedirectURL());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public OauthInformation login(Oauth type, String code) {
        OauthToken token = type.getAccessToken(code);
        String userInfoResponse = type.getUserInformation(token);
        OauthId id = OauthId.of(userInfoResponse, type);

        if (isNewUser(id)) {
            System.out.println("새로운 유저 입니다.");
            repository.save(new OauthInformation(id, token.getAccess_token()));
            return null;
        } else {
            System.out.println("존재하는 유저 입니다.");
            OauthInformation info = repository.findById(id).get();
            info.updateToken(token.getAccess_token());
            repository.save(info);
            return repository.findById(id).get();
        }
    }

    private boolean isNewUser(OauthId id) {
        return repository.findById(id).isEmpty();
    }
}
