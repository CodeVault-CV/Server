package com.example.algoproject.solution.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ListSolution {

    @NotNull
    private Long problemId;

    @NotNull
    private Long sessionId;
}
