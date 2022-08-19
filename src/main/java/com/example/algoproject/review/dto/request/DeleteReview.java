package com.example.algoproject.review.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteReview {

    @NotNull
    private String userId;
}
