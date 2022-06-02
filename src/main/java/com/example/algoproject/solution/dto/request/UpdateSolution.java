package com.example.algoproject.solution.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateSolution {

    @NotNull
    private Long problemId;

    @NotNull
    private String header;

    @NotNull
    private String content;

    @NotNull
    private String time;

    @NotNull
    private String memory;
}
