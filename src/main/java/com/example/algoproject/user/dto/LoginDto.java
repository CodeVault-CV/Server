package com.example.algoproject.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDto {

    @NotBlank
    private String token;

    @NotBlank
    private String id;

    public LoginDto(String token, String id) {
        this.token = token;
        this.id = id;
    }
}
