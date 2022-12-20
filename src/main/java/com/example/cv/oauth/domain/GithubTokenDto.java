package com.example.cv.oauth.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GithubTokenDto {
    @NotBlank
    private String access_token;

    @NotBlank
    private String scope;

    @NotBlank
    private String token_type;
}
