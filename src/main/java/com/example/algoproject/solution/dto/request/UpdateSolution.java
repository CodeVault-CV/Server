package com.example.algoproject.solution.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateSolution {

    @NotNull
    private Long problemId;

    @NotNull
    private String code;

    @NotNull
    private String readMe;

    @NotNull
    private String language;
}
