package com.example.algoproject.review.service;

import com.example.algoproject.review.domain.Review;
import com.example.algoproject.review.dto.AddReview;
import com.example.algoproject.review.dto.UpdateReview;
import com.example.algoproject.review.repository.ReviewRepository;
import com.example.algoproject.errors.exception.NotExistCommentException;
import com.example.algoproject.errors.exception.NotWriterUserException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.solution.service.SolutionService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ResponseService responseService;
    private final UserService userService;
    private final SolutionService solutionService;

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, AddReview request) {
        User user = userService.findByUserId(cudVO.getUsername());
        Solution solution = solutionService.findById(request.getSolutionId());

        Review review = new Review(user.getUserId(), request.getContent());

        reviewRepository.save(review);

        solution.addReview(review);

        solutionService.save(solution);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse update(CustomUserDetailsVO cudVO, UpdateReview request) {

        User user = userService.findByUserId(cudVO.getUsername());
        Review review = reviewRepository.findById(request.getId()).orElseThrow(NotExistCommentException::new);

        // 본인이 아닌 경우 글을 수정할 수 없음
        if(!review.getWriterId().equals(user.getUserId()))
            throw new NotWriterUserException();

        review.setContent(request.getContent());

        reviewRepository.save(review);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long commentId) {
        User user = userService.findByUserId(cudVO.getUsername());
        Review review = reviewRepository.findById(commentId).orElseThrow(NotExistCommentException::new);

        // 본인이 아닌 경우 글을 삭제할 수 없음
        if(!review.getWriterId().equals(user.getUserId()))
            throw new NotWriterUserException();

        reviewRepository.delete(review);

        return responseService.getSuccessResponse();
    }
}
