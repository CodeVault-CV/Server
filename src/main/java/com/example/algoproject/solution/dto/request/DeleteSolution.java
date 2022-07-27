package com.example.algoproject.solution.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
public class DeleteSolution {

    @NotBlank
    private String message;

    @Nullable
    private String sha;
}
