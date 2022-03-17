package com.example.algoproject.study.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddContributorRequest {

    @NotBlank
    private String permission;
}
