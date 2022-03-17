package com.example.algoproject.study.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StudyInfoResponse {

    @NotBlank
    String name;

    @NotBlank
    String url;

    @NotNull
    List<MemberInfoResponse> members;

    public StudyInfoResponse(String name, String url, List<MemberInfoResponse> members) {
        this.name = name;
        this.url = url;
        this.members = members;
    }
}
