package com.example.algoproject.study.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MemberInfoResponse {

    @NotBlank
    private String name;

    @NotNull
    private boolean accepted;

    public MemberInfoResponse(String name, boolean accepted) {
        this.name = name;
        this.accepted = accepted;
    }
}
