package com.example.cv.github.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
public class CommitDto {

    @NotBlank
    private String message;

    @NotBlank
    private String content;

    @Nullable
    private String sha;
}
