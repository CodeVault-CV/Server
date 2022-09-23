package com.example.algoproject.problem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteProblem {

    @NotNull(message = "세션 아이디는 필수 입니다.")
    private Long sessionId;

}
