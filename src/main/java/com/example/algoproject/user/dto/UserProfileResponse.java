package com.example.algoproject.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserProfileResponse {

    @NotBlank
    String name;

    String url;

    public UserProfileResponse(String name, String imageUrl) {
        this.name = name;
        this.url = imageUrl;
    }
}
