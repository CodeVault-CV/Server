package com.example.algoproject.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddComment {
    @NotNull
    private Long solutionId;

    @NotBlank
    private String content;
}
