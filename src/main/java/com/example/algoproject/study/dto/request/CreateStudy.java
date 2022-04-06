package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateStudy {

    @NotBlank(message = "스터디 이름은 필수 입력값입니다.")
    String studyName;

    @NotBlank(message = "레포지토리 이름은 필수 입력값입니다.")
    String repoName;
}
