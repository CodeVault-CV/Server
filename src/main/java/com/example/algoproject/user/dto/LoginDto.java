package com.example.algoproject.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDto {

    @NotBlank
    private String token;

    @NotBlank
    private String name;

    public LoginDto(String token, String name) {
        this.token = token;
        this.name = name;
    }
}
