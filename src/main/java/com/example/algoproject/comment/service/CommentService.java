package com.example.algoproject.comment.service;

import com.example.algoproject.comment.domain.Comment;
import com.example.algoproject.comment.dto.AddComment;
import com.example.algoproject.comment.dto.UpdateComment;
import com.example.algoproject.comment.repository.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final ResponseService responseService;
    private final UserService userService;
    private final SolutionService solutionService;

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, AddComment request) {
        User user = userService.findByUserId(cudVO.getUsername());
        Solution solution = solutionService.findById(request.getSolutionId());

        Comment comment = new Comment(user.getUserId(), request.getContent());

        commentRepository.save(comment);

        solution.addComment(comment);

        solutionService.save(solution);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse update(CustomUserDetailsVO cudVO, UpdateComment request) {

        User user = userService.findByUserId(cudVO.getUsername());
        Comment comment = commentRepository.findById(request.getId()).orElseThrow(NotExistCommentException::new);

        // 본인이 아닌 경우 글을 수정할 수 없음
        if(!comment.getWriterId().equals(user.getUserId()))
            throw new NotWriterUserException();

        comment.setContent(request.getContent());

        commentRepository.save(comment);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long commentId) {
        User user = userService.findByUserId(cudVO.getUsername());
        Comment comment = commentRepository.findById(commentId).orElseThrow(NotExistCommentException::new);

        // 본인이 아닌 경우 글을 삭제할 수 없음
        if(!comment.getWriterId().equals(user.getUserId()))
            throw new NotWriterUserException();

        commentRepository.delete(comment);

        return responseService.getSuccessResponse();
    }
}
