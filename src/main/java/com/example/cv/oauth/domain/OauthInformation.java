package com.example.cv.oauth.domain;

import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@NoArgsConstructor
@Entity
public class OauthInformation {

    @EmbeddedId
    private OauthId id;

    private String access_token;

    public OauthInformation(OauthId id, String access_token) {
        this.id = id;
        this.access_token = access_token;
    }

    public void updateToken(String access_token) {
        this.access_token = access_token;
    }

    public OauthId getId() {
        return id;
    }

    public String getAccess_token() {
        return access_token;
    }
}




































