package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateWebhook {

    @NotNull
    private String[] events = {"member", "repository"};

    @NotNull
    private WebhookConfig config = new WebhookConfig();
}
