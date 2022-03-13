package com.example.algoproject.study.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateRepositoryRequest {

    @NotBlank
    String name;

    @NotNull
    boolean auto_init;
}
