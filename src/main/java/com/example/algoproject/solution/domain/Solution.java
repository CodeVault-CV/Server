package com.example.algoproject.solution.domain;

import com.example.algoproject.comment.domain.Comment;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Solution {


    @Id
    @Column(name = "solution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problemId;

    private String codeUrl;

    private String readMeUrl;

    private Timestamp date; //등록 날짜/시간

    private String time; //시간복잡도

    private String memory; //공간복잡도

    private Language language; //사용 언어

    @OneToMany(
            mappedBy = "solution",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    public Solution(User userId, Problem problemId, String codeUrl, String readMeUrl, Timestamp date, String time, String memory, String language) {
        this.userId = userId;
        this.problemId = problemId;
        this.codeUrl = codeUrl;
        this.readMeUrl = readMeUrl;
        this.date = date;
        this.time = time;
        this.memory = memory;
        this.language = Language.valueOf(language);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        if(comment.getSolution() != this)
            comment.setSolution(this);
    }
}