package com.example.algoproject.solution.dto.response;

import com.example.algoproject.review.domain.Review;
import lombok.Data;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.List;

@Data
public class SolutionInfo {

    @NotBlank
    @Lob
    private String code;

    @NotBlank
    @Lob
    private String readMe;

    @NotBlank
    private Timestamp date; //등록 날짜/시간

    private List<Review> reviews;

    public SolutionInfo(String code, String readMe, Timestamp date, List<Review> reviews) {
        this.code = code;
        this.readMe = readMe;
        this.date = date;
        this.reviews = reviews;
    }
}
