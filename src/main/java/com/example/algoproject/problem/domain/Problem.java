package com.example.algoproject.problem.domain;

import com.example.algoproject.study.domain.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Problem {

    @Id
    @GeneratedValue
    private Long id;

    private String number;

    private String name;

    private String url;

    private Platform platform;

    private int week;

    @ElementCollection
    private List<String> types;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    public Problem(String number, String name, String url, String platform, int week, List<String> types) {
        this.number = number;
        this.name = name;
        this.url = url;
        this.platform = Platform.valueOf(platform);
        this.week = week;
        this.types = types;
    }

    public void setStudy(Study study) {
        this.study = study;

        // 스터디에 현재 문제가 존재하지 않는다면
        if(!study.getProblems().contains(this))
            // 문제 추가
            study.getProblems().add(this);
    }
}
