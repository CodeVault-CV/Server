package com.example.algoproject.solution.domain;

import com.example.algoproject.review.domain.Review;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.user.domain.User;
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
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Lob
    private String code;

    @Lob
    private String readMe;

    private Timestamp date; //등록 날짜/시간

    private Language language; //사용 언어

    private String codePath; // 깃허브의 코드 path

    private String readMePath; // 깃허브의 리드미 path

    @OneToMany(
            mappedBy = "solution",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Review> reviews = new ArrayList<>();

    public Solution(User user, Problem problem, String code, String readMe, Timestamp date, String language, String codePath, String readMePath) {
        this.user = user;
        this.problem = problem;
        this.code = code;
        this.readMe = readMe;
        this.date = date;
        this.language = Language.valueOf(language);
        this.codePath = codePath;
        this.readMePath = readMePath;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
        if(review.getSolution() != this)
            review.setSolution(this);
    }

    public void setProblem(Problem problem) {
        this.problem = problem;

        if(!problem.getSolutions().contains(this))
            problem.getSolutions().add(this);
    }
}