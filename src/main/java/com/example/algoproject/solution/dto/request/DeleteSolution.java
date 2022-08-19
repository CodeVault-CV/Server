package com.example.algoproject.solution.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DeleteSolution {

    @NotNull
    private String userId;

    @NotBlank
    private String message;

    @Nullable
    private String sha;
}
