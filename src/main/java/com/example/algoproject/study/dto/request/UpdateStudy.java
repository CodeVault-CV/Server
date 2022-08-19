package com.example.algoproject.study.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateStudy {

    @Length(min = 2, max = 10, message = "스터디의 이름은 2~10 글자여야 합니다")
    @NotNull(message = "스터디 이름은 필수 입력값입니다")
    private String name;
}
