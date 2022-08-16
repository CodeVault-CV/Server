package com.example.algoproject.review.service;

import com.example.algoproject.errors.exception.notfound.NotExistSolutionException;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.review.domain.Review;
import com.example.algoproject.review.dto.AddReview;
import com.example.algoproject.review.dto.UpdateReview;
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
        Problem problem = solution.getProblem();
        Study study = studyService.findById(problem.getSession().getStudy().getId());

        // 유저가 스터디에 속한 멤버인지 확인
        studyService.checkAuth(user, study);

        Review review = new Review(user.getId(), request.getContent());

        reviewRepository.save(review);

        solution.addReview(review);

        solutionService.save(solution);

        return responseService.getSuccessResponse();
    }

    @Transactional(readOnly = true)
    public CommonResponse list(CustomUserDetailsVO cudVO, Long solutionId) {

        User user = userService.findById(cudVO.getUsername());
        Solution solution = solutionService.findById(solutionId);
        Problem problem = solution.getProblem();
        Study study = studyService.findById(problem.getSession().getStudy().getId());

        // 유저가 스터디에 속한 멤버인지 확인
        studyService.checkAuth(user, study);

        return responseService.getListResponse(solution.getReviews());
    }

    @Transactional
    public CommonResponse update(CustomUserDetailsVO cudVO, UpdateReview request) {

        User user = userService.findById(cudVO.getUsername());
        Review review = reviewRepository.findById(request.getId()).orElseThrow(NotExistCommentException::new);

        // 본인이 아닌 경우 글을 수정할 수 없음
        if(!review.getWriterId().equals(user.getId()))
            throw new NotWriterUserException();

        review.setContent(request.getContent());

        reviewRepository.save(review);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long commentId) {
        User user = userService.findById(cudVO.getUsername());
        Review review = reviewRepository.findById(commentId).orElseThrow(NotExistCommentException::new);

        // 본인이 아닌 경우 글을 삭제할 수 없음
        if(!review.getWriterId().equals(user.getId()))
            throw new NotWriterUserException();

        reviewRepository.delete(review);

        return responseService.getSuccessResponse();
    }
}
