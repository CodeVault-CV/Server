package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MemberListRequest {

    @NotBlank
    private String ownerName;

    @NotBlank
    private String studyId;
}
