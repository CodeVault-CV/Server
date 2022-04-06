package com.example.algoproject.problem.dto.response;

import com.example.algoproject.problem.domain.Platform;
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

    @NotNull
    private int week;

    @NotNull
    private List<String> types;

    public ProblemInfo(String number, String name, String url, Platform platform, int week, List<String> types) {
        this.number = number;
        this.name = name;
        this.url = url;
        this.platform = platform;
        this.week = week;
        this.types = types;
    }
}
