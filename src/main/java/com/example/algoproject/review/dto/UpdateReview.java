package com.example.algoproject.review.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateReview {

    @NotBlank
    private String content;
}
