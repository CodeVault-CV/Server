package com.example.algoproject.problem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ProblemWeekList {

    @NotBlank
    String studyId;

    @NotNull
    @Positive
    int week;
}
