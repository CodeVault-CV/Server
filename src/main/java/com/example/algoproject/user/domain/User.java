package com.example.algoproject.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class User {
    @Id
    private String userId;

    private String name;

    private String accessToken;

    private String imageUrl;

    public User(String userId, String name, String accessToken, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.accessToken = accessToken;
        this.imageUrl = imageUrl;
    }

    public void update(String accessToken, String name, String imageUrl) {
        this.accessToken = accessToken;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}


