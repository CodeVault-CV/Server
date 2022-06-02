package com.example.algoproject.solution.dto.response;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
public class SolutionInfo {

    @NotBlank
    private Long solutionId;

    @NotBlank
    private String codeUrl;

    @NotBlank
    private String readMeUrl;

    @NotBlank
    private Timestamp date; //등록 날짜/시간

    @NotBlank
    private String time; //시간복잡도

    @NotBlank
    private String memory; //공간복잡도

    public SolutionInfo(Long solutionId, String codeUrl, String readMeUrl, Timestamp date, String time, String memory) {
        this.solutionId = solutionId;
        this.codeUrl = codeUrl;
        this.readMeUrl = readMeUrl;
        this.date = date;
        this.time = time;
        this.memory = memory;
    }
}
