package com.example.algoproject.problem.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class AddProblem {

    @NotNull(message = "study_id가 공백입니다")
    private String studyId;

    @Nullable
    private String number;

    @NotNull(message = "문제 이름은 필수 입력 값입니다")
    private String name;

    @NotNull(message = "문제 주소는 필수 입력 값입니다")
    private String url;

    @NotNull(message = "주차는 필수 입력 값입니다")
    @Positive
    private int week;

    @NotNull(message = "알고리즘 플랫폼은 필수 선택값입니다")
    private String platform;

    @Nullable
    private List<String> types;
}
