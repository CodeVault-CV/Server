package com.example.algoproject.solution.dto.response;

import com.example.algoproject.review.domain.Review;
import com.example.algoproject.solution.domain.Language;
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

    private Language language;

    @NotBlank
    private Timestamp date; //등록 날짜/시간

    private String id;

    private String name;

    public SolutionInfo(String code, String readMe, Language language, Timestamp date, String id, String name) {
        this.code = code;
        this.readMe = readMe;
        this.language = language;
        this.date = date;
        this.id = id;
        this.name = name;
    }
}
