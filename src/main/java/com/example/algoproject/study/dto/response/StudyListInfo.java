package com.example.algoproject.study.dto.response;

import com.example.algoproject.study.domain.Study;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StudyListInfo {

    @NotBlank
    private String id;

    @NotNull
    private String name;

    public StudyListInfo(Study study) {
        this.id = study.getId();
        this.name = study.getName();
    }
}
