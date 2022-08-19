package com.example.algoproject.solution.dto.response;

import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.user.domain.User;
import lombok.Data;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class SolutionInfo {

    @NotNull
    private Long id;

    @NotBlank
    @Lob
    private String code;

    @NotBlank
    @Lob
    private String readMe;

    @NotBlank
    private Timestamp date; //등록 날짜/시간

    private String userId;

    private String userName;

    public SolutionInfo(Solution solution, User user) {
        this.id = solution.getId();
        this.code = solution.getCode();
        this.readMe = solution.getReadMe();
        this.date = solution.getDate();
        this.userId = user.getId();
        this.userName = user.getName();
    }
}
