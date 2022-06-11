package com.example.algoproject.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateComment {
    @NotNull
    private Long id;

    @NotBlank
    private String content;
}
