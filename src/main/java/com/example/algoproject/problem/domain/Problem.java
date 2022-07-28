package com.example.algoproject.problem.domain;

import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.study.domain.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Problem {

    @Id
    @Column(name = "problem_id")
    @GeneratedValue
    private Long id;

    private String number;

    private String name;

    private String url;

    private Platform platform;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    @OneToMany(
            mappedBy = "problem",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private List<Solution> solutions = new ArrayList<>();

    public Problem(AddProblem request) {
        this.number = request.getNumber();
        this.name = request.getName();
        this.url = setUrl(request.getPlatform(), request.getNumber());
        this.platform = Platform.valueOf(request.getPlatform());
    }

    private String setUrl(String platform, String number) {
        StringBuilder sb = new StringBuilder();

        if (platform.equals("Programmers"))
            sb.append("https://programmers.co.kr/learn/courses/30/lessons/");
        else
            sb.append("https://www.acmicpc.net/problem/");

        return sb.append(number).toString();
    }

    public void setSession(Session session) {
        this.session = session;

        if(!session.getProblems().contains(this))
            session.getProblems().add(this);
    }
}
