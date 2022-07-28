package com.example.algoproject.session.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class CreateSession {
    @NotNull(message = "스터디 ID는 필수 입력값입니다")
    private String studyId;

    @NotNull(message = "세션 이름은 필수 입력값입니다")
    private String name;

    @NotNull(message = "세션의 시작 날짜는 필수 입력값입니다")
    private Date start;

    @NotNull(message = "세션의 끝 날짜는 필수 입력값입니다")
    private Date end;
}
