package com.example.algoproject.review.controller;

import com.example.algoproject.review.dto.request.AddReview;
import com.example.algoproject.review.dto.request.UpdateReview;
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

    @Operation(summary = "리뷰 조회", description = "성공시 리뷰 list 반환")
    @GetMapping("/{solutionId}")
    public CommonResponse reviewList(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("solutionId") Long solutionId) {
        return reviewService.list(cudVO, solutionId);
    }

    @Operation(summary = "리뷰 수정", description = "성공시 메세지, 코드만 반환")
    @PutMapping("/{id}")
    public CommonResponse reviewModify(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody UpdateReview request, @PathVariable Long id) {
        return reviewService.update(cudVO, request, id);
    }

    @Operation(summary = "리뷰 삭제", description = "성공시 메세지, 코드만 반환")
    @DeleteMapping("/{reviewId}")
    public CommonResponse reviewRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable Long reviewId) {
        return reviewService.delete(cudVO, reviewId);
    }
}
