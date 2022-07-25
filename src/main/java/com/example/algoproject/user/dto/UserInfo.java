package com.example.algoproject.user.dto;

import com.example.algoproject.user.domain.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserInfo {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private String githubUrl;

    public UserInfo(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.imageUrl = user.getImageUrl();
        this.githubUrl = user.getGithubUrl();
    }
}
