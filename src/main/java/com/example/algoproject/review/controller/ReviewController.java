package com.example.algoproject.review.controller;

import com.example.algoproject.review.dto.AddReview;
import com.example.algoproject.review.dto.UpdateReview;
import com.example.algoproject.review.service.ReviewService;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/review")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @ApiOperation(value = "리뷰 추가", notes = "성공시 메세지, 코드만 반환")
    @PostMapping()
    public CommonResponse reviewAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody AddReview request) {
        return reviewService.create(cudVO, request);
    }

    @ApiOperation(value = "리뷰 수정", notes = "성공시 메세지, 코드만 반환")
    @PutMapping()
    public CommonResponse reviewModify(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody UpdateReview request) {
        return reviewService.update(cudVO, request);
    }

    @ApiOperation(value = "리뷰 삭제", notes = "성공시 메세지, 코드만 반환")
    @DeleteMapping("/{reviewId}")
    public CommonResponse reviewRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable Long reviewId) {
        return reviewService.delete(cudVO, reviewId);
    }
}
