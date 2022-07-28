package com.example.algoproject.study.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateWebhook {

//    @NotNull
//    private String[] events = {"member", "repository", "push"};

    @NotNull
    private String[] events;

    @NotNull
    private WebhookConfig config = new WebhookConfig();

    public CreateWebhook(String[] events) {
        this.events = events;
    }

}
