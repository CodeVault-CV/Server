package com.example.cv.user.service;

import com.example.cv.oauth.domain.Oauth;
import com.example.cv.user.domain.User;
import com.example.cv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${default.user.name}")
    private String defaultName;
    @Value("${default.user.profile_url}")
    private String defaultProfileUrl;

    private final UserRepository repository;

    @Transactional
    public User create(Oauth oauth) {
        return repository.save(new User(defaultName, defaultProfileUrl));
    }

    @Transactional
    public void update() {

    }

}
