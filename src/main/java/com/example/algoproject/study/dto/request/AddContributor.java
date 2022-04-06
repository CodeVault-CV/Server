package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddContributor {

    @NotBlank
    private String permission;
}
