package com.example.algoproject.session.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UpdateSession {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Date start;

    @NotNull
    private Date end;
}
