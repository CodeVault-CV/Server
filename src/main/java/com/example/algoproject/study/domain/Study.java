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
    String studyId;

    String name;

    String leaderId;

    String repositoryUrl;

    public Study(String studyId, String name, String leaderId, String repositoryUrl) {
        this.studyId = studyId;
        this.name = name;
        this.leaderId = leaderId;
        this.repositoryUrl = repositoryUrl;
    }
}
