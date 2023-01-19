package com.example.cv.oauth.domain;

import com.example.cv.user.domain.User;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class Oauth {

    @Column(name = "oauth_id")
    @EmbeddedId
    private OauthId id;

    private String access_token;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    public Oauth(OauthId id, String access_token) {
        this.id = id;
        this.access_token = access_token;
    }

    public void updateToken(String access_token) {
        this.access_token = access_token;
    }

    public Long getUserId() {
        return user.getId();
    }

    public void setUser(User user) {
        this.user = user;
    }
}