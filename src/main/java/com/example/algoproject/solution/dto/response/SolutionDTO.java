package com.example.algoproject.solution.dto.response;

import com.example.algoproject.solution.domain.Language;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
public class SolutionDTO {

    @NotBlank
    private Long id;

    private String user;

    private String code;

    private String readMe;

    private Timestamp date;

    private Language language;

    public SolutionDTO(Long id, String user, String code, String readMe, Timestamp date, String language) {
        this.id = id;
        this.user = user;
        this.code = code;
        this.readMe = readMe;
        this.date = date;
        this.language = Language.valueOf(language);
    }
}
