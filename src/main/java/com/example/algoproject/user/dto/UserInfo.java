package com.example.algoproject.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserInfo {

    @NotBlank
    private String name;

    private String url;

    public UserInfo(String name, String imageUrl) {
        this.name = name;
        this.url = imageUrl;
    }
}
