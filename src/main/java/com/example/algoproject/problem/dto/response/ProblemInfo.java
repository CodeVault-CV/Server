package com.example.algoproject.problem.dto.response;

import com.example.algoproject.problem.domain.Platform;
import com.example.algoproject.problem.domain.Problem;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProblemInfo {
    @NotNull
    private Long id;

    @NotBlank
    private String number;

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotNull
    private Platform platform;

    public ProblemInfo(Problem problem) {
        this.id = problem.getId();
        this.number = problem.getNumber();
        this.name = problem.getName();
        this.url = problem.getUrl();
        this.platform = problem.getPlatform();
    }
}
