package com.example.algoproject.user.domain;

import lombok.Getter;

@Getter
public class TokenResponse {
    String access_token;
    String scope;
    String token_type;
}
