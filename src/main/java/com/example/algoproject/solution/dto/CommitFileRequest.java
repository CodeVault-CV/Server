package com.example.algoproject.solution.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommitFileRequest {

    @NotBlank
    private String message;

    @NotBlank
    private String content;
    // sha는 null 일 수도 있음
    private String sha;
}
