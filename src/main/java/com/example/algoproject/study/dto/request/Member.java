package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Member {

    @NotBlank
    private String member;

    @NotBlank
    private String studyId;
}
