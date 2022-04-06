package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MemberList {

    @NotBlank
    private String ownerName;

    @NotBlank
    private String studyId;
}
