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

    public Study(String studyId, String name, String leaderId) {
        this.studyId = studyId;
        this.name = name;
        this.leaderId = leaderId;
    }
}
