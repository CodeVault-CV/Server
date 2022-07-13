package com.example.algoproject.study.domain;

import com.example.algoproject.session.domain.Session;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Study {

    @Id
    @Column(name = "study_id")
    private String studyId;

    private String name;

    private String leaderId;

    private String repositoryName;

    private String repositoryUrl;

    @OneToMany(
            mappedBy = "study",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private List<Session> sessions = new ArrayList<>();

    public Study(String studyId, String name, String leaderId, String repositoryName, String repositoryUrl) {
        this.studyId = studyId;
        this.name = name;
        this.leaderId = leaderId;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
    }

    public void addSession(Session session) {
        this.sessions.add(session);

        if(session.getStudy() != this)
            session.setStudy(this);
    }
}
