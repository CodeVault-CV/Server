package com.example.algoproject.review.dto.response;

import com.example.algoproject.review.domain.Review;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class ReviewInfo {

    @NotNull
    private Long id;

    @NotNull
    private String writerId;

    @NotNull
    private String writerName;

    @NotNull
    private String content;

    @NotNull
    private Timestamp createdTime;

    @NotNull
    private Timestamp updatedTime;

    public ReviewInfo(Review review) {
        this.id = review.getId();
        this.writerId = review.getWriterId();
        this.writerName = review.getWriterName();
        this.content = review.getContent();
        this.createdTime = review.getCreatedTime();
        this.updatedTime = review.getUpdatedTime();
    }
}
