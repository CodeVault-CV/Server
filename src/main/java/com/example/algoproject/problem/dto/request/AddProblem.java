package com.example.algoproject.problem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddProblem {

    @NotNull(message = "세션 아이디는 필수 입니다.")
    private Long sessionId;

    @NotNull(message = "문제 번호는 필수 입력 값 입니다.")
    private String number;

    @NotNull(message = "문제 이름은 필수 입력 값 입니다")
    private String name;

    @NotNull(message = "알고리즘 플랫폼은 필수 선택 값 입니다")
    private String platform;
}
