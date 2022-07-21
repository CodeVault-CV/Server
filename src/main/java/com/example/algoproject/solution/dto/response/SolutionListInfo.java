package com.example.algoproject.solution.dto.response;

import com.example.algoproject.solution.domain.Language;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SolutionListInfo {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotBlank
    private boolean solve; // false: 안품, true: 품

    @Nullable
    private Language language; // solve가 flase면 blank(="") 반환

    public SolutionListInfo(String name, String url, boolean solve, String language) {
        this.name = name;
        this.url = url;
        this.solve = solve;
        this.language = Language.valueOf(language);
    }
}
