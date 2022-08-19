package com.example.algoproject.review.service;

import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.review.domain.Review;
import com.example.algoproject.review.dto.request.AddReview;
import com.example.algoproject.review.dto.request.UpdateReview;
import com.example.algoproject.review.dto.response.ReviewInfo;
import com.example.algoproject.review.repository.ReviewRepository;
import com.example.algoproject.errors.exception.notfound.NotExistCommentException;
import com.example.algoproject.errors.exception.badrequest.NotWriterUserException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.solution.service.SolutionService;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ResponseService responseService;
    private final UserService userService;
    private final StudyService studyService;
    private final SolutionService solutionService;

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, AddReview request) {
        User user = userService.findById(cudVO.getUsername());
        Solution solution = solutionService.findById(request.getSolutionId());

        Review review = new Review(user.getId(), user.getName(), request.getContent());
        reviewRepository.save(review);
        solution.addReview(review);
        solutionService.save(solution);

        return responseService.getSingleResponse(new ReviewInfo(review));
    }

    @Transactional(readOnly = true)
    public CommonResponse list(Long solutionId) {
        return responseService.getListResponse(getReviewInfos(solutionService.findById(solutionId)));
    }

    @Transactional
    public CommonResponse update(CustomUserDetailsVO cudVO, UpdateReview request, Long id) {

        User user = userService.findById(cudVO.getUsername());
        Review review = findById(id);

        // 본인이 아닌 경우 글을 수정할 수 없음
        if (!review.getUserId().equals(user.getId()))
            throw new NotWriterUserException();

        review.setContent(request.getContent());

        reviewRepository.save(review);

        return responseService.getSingleResponse(new ReviewInfo(review));
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long id) {
        User user = userService.findById(cudVO.getUsername());
        Review review = findById(id);

        // 본인이 아닌 경우 글을 삭제할 수 없음
        if (!review.getUserId().equals(user.getId()))
            throw new NotWriterUserException();

        reviewRepository.delete(review);

        return responseService.getSuccessResponse();
    }

    @Transactional(readOnly = true)
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow(NotExistCommentException::new);
    }

    @Transactional(readOnly = true)
    public List<ReviewInfo> getReviewInfos(Solution solution) {
        return solution.getReviews().stream().map(ReviewInfo::new).collect(Collectors.toList());
    }
}
