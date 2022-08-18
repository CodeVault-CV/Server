package com.example.algoproject.review.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddReview {
    @NotNull
    private Long solutionId;

    @NotBlank
    private String content;
}
