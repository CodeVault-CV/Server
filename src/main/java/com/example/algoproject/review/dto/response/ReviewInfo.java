package com.example.algoproject.review.dto.response;

import com.example.algoproject.review.domain.Review;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ReviewInfo {

    @NotNull
    private Long id;

    @NotNull
    private String userId;

    @NotNull
    private String userName;

    @NotNull
    private String content;

    @NotNull
    private LocalDateTime createdTime;

    @NotNull
    private LocalDateTime updatedTime;

    public ReviewInfo(Review review) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.userName = review.getUserName();
        this.content = review.getContent();
        this.createdTime = review.getCreatedTime();
        this.updatedTime = review.getUpdatedTime();
    }
}
