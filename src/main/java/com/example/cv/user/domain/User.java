package com.example.cv.user.domain;

import com.example.cv.oauth.domain.Oauth;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
public class User {

    @Column(name = "user_id")
    @GeneratedValue
    @Id
    private Long id;

    private String name;

    private String profileUrl;

    @OneToMany
    @JoinColumn(name = "oauth_id")
    private List<Oauth> oauths;

    public User(String name, String profileUrl) {
        this.name = name;
        this.profileUrl = profileUrl;
        this.oauths = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void addOauth(Oauth oauth) {
        oauths.add(oauth);
    }
}
