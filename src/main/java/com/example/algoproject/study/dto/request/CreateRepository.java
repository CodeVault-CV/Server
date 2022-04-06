package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateRepository {

    @NotBlank
    private String name;

    @NotNull
    private boolean auto_init;
}
