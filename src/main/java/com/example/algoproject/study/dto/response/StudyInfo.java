package com.example.algoproject.study.dto.response;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StudyInfo {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotNull
    private List<MemberInfo> members;

    public StudyInfo(String name, String url, List<MemberInfo> members) {
        this.name = name;
        this.url = url;
        this.members = members;
    }
}
