package com.example.algoproject.solution.dto.request;

import lombok.Data;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

@Data
public class UpdateSolution {

    @NotNull
    private Long problemId;

    @NotNull
    private String userId;

    @NotNull
    @Lob
    private String code;

    @NotNull
    @Lob
    private String readMe;

    @NotNull
    private String language;
}
