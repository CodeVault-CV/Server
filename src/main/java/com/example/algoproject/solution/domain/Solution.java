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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private String codeUrl;

    private String readMeUrl;

    private Timestamp date; //등록 날짜/시간

    private String time; //시간복잡도

    private String memory; //공간복잡도

    @OneToMany(
            mappedBy = "solution",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Review> reviews = new ArrayList<>();

    public Solution(User user, Problem problem, String codeUrl, String readMeUrl, Timestamp date, String time, String memory) {
        this.user = user;
        this.problem = problem;
        this.codeUrl = codeUrl;
        this.readMeUrl = readMeUrl;
        this.date = date;
        this.time = time;
        this.memory = memory;
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
