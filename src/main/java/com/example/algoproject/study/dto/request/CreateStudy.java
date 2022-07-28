package com.example.algoproject.study.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CreateStudy {

    @Length(min = 2, max = 10, message = "스터디의 이름은 2~10 글자여야 합니다")
    @NotNull(message = "스터디 이름은 필수 입력값입니다")
    String studyName;

    @Pattern(regexp = "[a-zA-z0-9,\\-]{1,39}", message = "저장소의 이름은 1~39 글자의 영어대•소문자, -, _ 만 사용이 가능합니다")
    @NotBlank(message = "레포지토리 이름은 필수 입력값입니다")
    String repoName;
}
