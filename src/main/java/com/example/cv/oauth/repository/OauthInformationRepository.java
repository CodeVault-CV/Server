package com.example.cv.oauth.repository;

import com.example.cv.oauth.domain.Oauth;
import com.example.cv.oauth.domain.OauthId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthInformationRepository extends JpaRepository<Oauth, OauthId> {

    Optional<Oauth> findById(OauthId id);
}
