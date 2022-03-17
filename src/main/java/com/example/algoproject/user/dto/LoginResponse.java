package com.example.algoproject.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginResponse {

    @NotBlank
    private String token;

    @NotBlank
    private String name;

    public LoginResponse(String token, String name) {
        this.token = token;
        this.name = name;
    }
}
