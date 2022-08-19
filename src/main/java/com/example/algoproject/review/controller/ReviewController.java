package com.example.algoproject.review.controller;

import com.example.algoproject.review.dto.request.AddReview;
import com.example.algoproject.review.dto.request.DeleteReview;
import com.example.algoproject.review.dto.request.UpdateReview;
import com.example.algoproject.review.service.ReviewService;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.util.Auth;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.algoproject.util.Auth.Role.*;

@RequiredArgsConstructor
@RequestMapping("/api/review")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Auth(role = MEMBER)
    @Operation(summary = "리뷰 추가", description = "성공시 리뷰 정보 반환")
    @PostMapping()
    public CommonResponse reviewAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody AddReview request) {
        return reviewService.create(cudVO, request);
    }

    @Auth(role = MEMBER)
    @Operation(summary = "리뷰 조회", description = "성공시 리뷰 list 반환")
    @GetMapping("/{solutionId}")
    public CommonResponse reviewList(@AuthenticationPrincipal @PathVariable("solutionId") Long solutionId) {
        return reviewService.list(solutionId);
    }

    @Auth(role = MEMBER)
    @Operation(summary = "리뷰 수정", description = "성공시 리뷰 정보 반환")
    @PutMapping("/{id}")
    public CommonResponse reviewModify(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody UpdateReview request, @PathVariable Long id) {
        return reviewService.update(cudVO, request, id);
    }

    @Auth(role = MEMBER)
    @Operation(summary = "리뷰 삭제", description = "성공시 메세지, 코드만 반환")
    @DeleteMapping("/{id}")
    public CommonResponse reviewRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody DeleteReview request, @PathVariable Long id) {
        return reviewService.delete(cudVO, id);
    }
}
