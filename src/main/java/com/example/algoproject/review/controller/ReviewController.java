package com.example.algoproject.review.controller;

import com.example.algoproject.review.dto.AddReview;
import com.example.algoproject.review.dto.UpdateReview;
import com.example.algoproject.review.service.ReviewService;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/review")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 추가", description = "성공시 메세지, 코드만 반환")
    @PostMapping()
    public CommonResponse reviewAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody AddReview request) {
        return reviewService.create(cudVO, request);
    }

    @Operation(summary = "리뷰 수정", description = "성공시 메세지, 코드만 반환")
    @PutMapping()
    public CommonResponse reviewModify(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody UpdateReview request) {
        return reviewService.update(cudVO, request);
    }

    @Operation(summary = "리뷰 삭제", description = "성공시 메세지, 코드만 반환")
    @DeleteMapping("/{reviewId}")
    public CommonResponse reviewRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable Long reviewId) {
        return reviewService.delete(cudVO, reviewId);
    }
}
