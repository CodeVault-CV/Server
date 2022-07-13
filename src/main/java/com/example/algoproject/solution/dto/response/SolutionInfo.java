package com.example.algoproject.solution.dto.response;

import com.example.algoproject.review.domain.Review;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.List;

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

    private List<Review> reviews;

    public SolutionInfo(Long solutionId, String codeUrl, String readMeUrl, Timestamp date, String time, String memory, List<Review> reviews) {
        this.solutionId = solutionId;
        this.codeUrl = codeUrl;
        this.readMeUrl = readMeUrl;
        this.date = date;
        this.time = time;
        this.memory = memory;
        this.reviews = reviews;
    }
}
