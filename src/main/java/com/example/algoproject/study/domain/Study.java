package com.example.algoproject.study.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    public Study(String studyId, String name, String leaderId, String repositoryName, String repositoryUrl) {
        this.studyId = studyId;
        this.name = name;
        this.leaderId = leaderId;
        this.repositoryName = repositoryName;
        this.repositoryUrl = repositoryUrl;
    }
}
