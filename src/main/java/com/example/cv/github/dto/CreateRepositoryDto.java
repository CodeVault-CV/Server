package com.example.cv.github.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateRepositoryDto {

    @NotBlank
    private String name;

    @NotNull
    private boolean auto_init;
}
