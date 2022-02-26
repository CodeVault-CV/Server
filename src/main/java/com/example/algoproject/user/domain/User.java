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

    private String accessToken;
}
