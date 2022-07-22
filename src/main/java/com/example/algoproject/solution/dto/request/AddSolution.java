package com.example.algoproject.solution.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

@Data
public class AddSolution {

    @NotNull
    private Long problemId;

    @NotNull
    @Lob
    private String code;

    @NotNull
    @Lob
    private String readMe;

    @NotNull
    private String language;
}
