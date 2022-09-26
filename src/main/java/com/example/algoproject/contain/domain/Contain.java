package com.example.algoproject.contain.domain;

import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.solution.domain.Solution;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Contain {

    @Id
    @Column(name = "contain_id")
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @OneToMany(
            mappedBy = "contain",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private List<Solution> solutions = new ArrayList<>();

    public Contain(Session session, Problem problem) {
        this.session = session;
        this.problem = problem;
    }
}
