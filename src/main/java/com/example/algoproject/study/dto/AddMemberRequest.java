package com.example.algoproject.study.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddMemberRequest {

    @NotBlank
    String memberName;

    @NotBlank
    String repoName;
}