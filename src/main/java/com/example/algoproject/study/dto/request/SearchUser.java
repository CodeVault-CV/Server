package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SearchUser {

    @NotNull(message = "검색할 유저 이름은 필수 입력값입니다")
    @Pattern(regexp = "[a-zA-Z0-9,\\-]{1,39}")
    private String name;

    @NotNull(message = "스터디 ID는 필수 입력값입니다")
    private String id;
}
