package com.example.algoproject.problem.dto.response;

import com.example.algoproject.problem.domain.Platform;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.solution.domain.Solution;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ProblemInfo {

    @NotBlank
    private String number;

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotNull
    private Platform platform;

    public ProblemInfo(Problem problem) {
        this.number = problem.getNumber();
        this.name = problem.getName();
        this.url = problem.getUrl();
        this.platform = problem.getPlatform();
    }
}
