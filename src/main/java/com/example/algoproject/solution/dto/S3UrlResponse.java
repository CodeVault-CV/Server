package com.example.algoproject.solution.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class S3UrlResponse {

    @NotBlank
    private String codeUrl;

    @NotBlank
    private String readMeUrl;

    public S3UrlResponse(String codeUrl, String readMeUrl) {
        this.codeUrl = codeUrl;
        this.readMeUrl = readMeUrl;
    }
}