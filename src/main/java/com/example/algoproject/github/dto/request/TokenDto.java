package com.example.algoproject.github.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TokenDto {

    @NotBlank
    private String access_token;

    @NotBlank
    private String scope;

    @NotBlank
    private String token_type;
}
