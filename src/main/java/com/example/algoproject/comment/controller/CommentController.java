package com.example.algoproject.comment.controller;

import com.example.algoproject.comment.dto.AddComment;
import com.example.algoproject.comment.dto.UpdateComment;
import com.example.algoproject.comment.service.CommentService;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/comment")
@RestController
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "코멘트 추가", notes = "성공시 메세지, 코드만 반환")
    @PostMapping()
    public CommonResponse commentAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody AddComment request) {
        return commentService.create(cudVO, request);
    }

    @ApiOperation(value = "코멘트 수정", notes = "성공시 메세지, 코드만 반환")
    @PutMapping()
    public CommonResponse commentModify(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody UpdateComment request) {
        return commentService.update(cudVO, request);
    }

    @ApiOperation(value = "코멘트 삭제", notes = "성공시 메세지, 코드만 반환")
    @DeleteMapping("/{commentId}")
    public CommonResponse commentRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable Long commentId) {
        return commentService.delete(cudVO, commentId);
    }
}
