package com.example.algoproject.problem.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    private int level;

    private String url;

    private Platform platform;

    public Problem(String number, String name, int level, Platform platform) {
        this.number = number;
        this.name = name;
        this.level = level;
        this.platform = platform;
        this.url = this.platform.getUrl() + number;
    }
}
