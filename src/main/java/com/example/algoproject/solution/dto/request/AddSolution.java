package com.example.algoproject.solution.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class AddSolution {

    @NotNull
    private Long problemId;

    @NotNull
    private String header;

    @NotNull
    private String content;

    @NotNull
    private String time;

    @NotNull
    private String memory;
}
