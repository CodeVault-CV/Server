package com.example.algoproject.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginResponse {

    @NotBlank
    String token;

    @NotBlank
    String name;

    public LoginResponse(String token, String name) {
        this.token = token;
        this.name = name;
    }
}
