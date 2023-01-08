package com.example.cv.oauth.repository;

import com.example.cv.oauth.domain.OauthId;
import com.example.cv.oauth.domain.OauthInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthInformationRepository extends JpaRepository<OauthInformation, OauthId> {

    Optional<OauthInformation> findById(OauthId id);
}
