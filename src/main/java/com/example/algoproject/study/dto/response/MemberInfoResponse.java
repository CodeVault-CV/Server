package com.example.algoproject.study.dto.response;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MemberInfoResponse {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotNull
    private boolean accepted;

    public MemberInfoResponse(String name, String url, boolean accepted) {
        this.name = name;
        this.url = url;
        this.accepted = accepted;
    }
}
