package com.example.algoproject.solution.dto.response;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SolutionId {

    @NotBlank
    private Long id;

    public SolutionId(Long id) {
        this.id = id;
    }
}
