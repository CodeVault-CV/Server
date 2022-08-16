package com.example.algoproject.solution.dto.response;

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

    public SolutionDTO(Long id, String user, String code, String readMe, Timestamp date) {
        this.id = id;
        this.user = user;
        this.code = code;
        this.readMe = readMe;
        this.date = date;
    }
}
