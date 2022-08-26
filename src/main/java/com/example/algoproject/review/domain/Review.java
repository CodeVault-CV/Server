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
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    private String userId;

    private String userName;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @Column(updatable = false)
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "solution_id")
    private Solution solution;

    public Review(String userId, String userName, String content) {
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        if(!solution.getReviews().contains(this))
            solution.getReviews().add(this);
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedTime = LocalDateTime.now();
    }
}
