package com.example.algoproject.have.domain;

import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.tag.domain.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Have {

    @Id
    @Column(name = "contain_id")
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public Have(Problem problem, Tag tag) {
        this.problem = problem;
        this.tag = tag;
    }
}
