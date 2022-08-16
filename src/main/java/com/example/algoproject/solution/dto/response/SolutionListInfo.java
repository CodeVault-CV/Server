package com.example.algoproject.solution.dto.response;

import com.example.algoproject.solution.domain.Language;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SolutionListInfo {

    @NotBlank
    private boolean solve; // false: 안품, true: 품

    @Nullable
    private Long solutionId;

    @NotNull
    private String userId;

    @NotBlank
    private String name;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private Language language; // solve가 flase면 blank(="") 반환

    public SolutionListInfo(boolean solve, Long solutionId, String userId, String name, String imageUrl, String language) {
        this.solve = solve;
        this.solutionId = solutionId;
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.language = Language.valueOf(language);
    }
}
