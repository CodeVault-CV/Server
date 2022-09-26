package com.example.algoproject.session.domain;

import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.session.dto.request.CreateSession;
import com.example.algoproject.session.dto.request.UpdateSession;
import com.example.algoproject.study.domain.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Session {

    @Id
    @Column(name = "session_id")
    @GeneratedValue
    private Long id;

    private String name;

    private Date start;

    private Date end;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    public Session(CreateSession request) {
        this.name = request.getName();
        this.start = request.getStart();
        this.end = request.getEnd();
    }

    public void update(UpdateSession request) {
        this.name = request.getName();
        this.start = request.getStart();
        this.end = request.getEnd();
    }

    public void setStudy(Study study) {
        this.study = study;

        if(!study.getSessions().contains(this))
            study.getSessions().add(this);
    }
}
