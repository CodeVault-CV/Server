package com.example.algoproject.review.domain;

import com.example.algoproject.solution.domain.Solution;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    private String writerId;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @UpdateTimestamp
    private Timestamp updatedTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "solution_id")
    private Solution solution;

    public Review(String writerId, String content) {
        this.writerId = writerId;
        this.content = content;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        if(!solution.getReviews().contains(this))
            solution.getReviews().add(this);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
