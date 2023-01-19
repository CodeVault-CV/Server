package com.example.cv.oauth.service;

import com.example.cv.oauth.domain.*;
import com.example.cv.oauth.repository.OauthInformationRepository;
import com.example.cv.user.domain.User;
import com.example.cv.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final OauthInformationRepository repository;
    private final UserService userService;
    private final HttpServletResponse response;

    public void request(SocialOauth type) {
        try {
            response.sendRedirect(type.getRedirectURL());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Transactional
    public OauthResponse login(SocialOauth type, String code) {
        OauthToken token = type.getAccessToken(code);
        OauthId id = OauthId.of(type.getUserInformation(token), type);

        if (isNewUser(id)) {
            return new OauthResponse(createUser(token, id), "http://localhost:3000/user/update");
        } else {
            return new OauthResponse(updateToken(token, id), "http://localhost:3000/");
        }
    }

    private Long createUser(OauthToken token, OauthId id) {
        Oauth oauth = new Oauth(id, token.getAccess_token());
        User user = userService.create(oauth);
        oauth.setUser(user);
        repository.save(oauth);
        user.addOauth(oauth);
        return user.getId();
    }

    private Long updateToken(OauthToken token, OauthId id) {
        Oauth oauth = repository.findById(id).get();
        oauth.updateToken(token.getAccess_token());
        repository.save(oauth);
        return oauth.getUserId();
    }

    private boolean isNewUser(OauthId id) {
        return repository.findById(id).isEmpty();
    }
}
