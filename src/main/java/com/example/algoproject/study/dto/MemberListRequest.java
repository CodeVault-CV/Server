package com.example.algoproject.study.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MemberListRequest {

    @NotBlank
    String ownerName;

    @NotBlank
    String repoName;
}
