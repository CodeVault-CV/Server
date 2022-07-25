package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WebhookConfig {

    @NotNull
    private String url;

    @NotNull
    private String content_type = "json";
}
