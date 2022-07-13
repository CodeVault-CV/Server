package com.example.algoproject.session.dto.response;

import com.example.algoproject.session.domain.Session;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SessionInfo {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Date start;

    @NotNull
    private Date end;

    public SessionInfo(Session session) {
        this.id = session.getId();
        this.name = session.getName();
        this.start = session.getStart();
        this.end = session.getEnd();
    }
}
