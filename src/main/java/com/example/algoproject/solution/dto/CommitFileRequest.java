package com.example.algoproject.solution.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
public class CommitFileRequest {

    @NotBlank
    private String message;

    @NotBlank
    private String content;

    @Nullable
    private String sha;
}
