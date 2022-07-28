package com.example.algoproject.problem.controller;

import com.example.algoproject.errors.response.*;
import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.problem.service.ProblemService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/problem")
@RestController
public class ProblemController {

    private final ProblemService problemService;

    @Operation(summary="문제 추가(팀장만 가능)", description="세션 ID, 문제의 번호, 이름, 플랫폼을 받아 문제의 정보 반환")
    @PostMapping()
    public CommonResponse problemAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid AddProblem request) {
        return problemService.create(cudVO, request);
    }

    @Operation(summary = "세션에 등록 된 모든 문제 보기", description = "세션 ID로 문제 정보들의 리스트 반환")
    @GetMapping("/list/{sessionId}")
    public CommonResponse problemList(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable Long sessionId) {
        return problemService.list(cudVO, sessionId);
    }

    @Operation(summary = "문제 삭제(팀장만 가능)", description = "문제 ID로 문제를 삭제후 성공 여부만 반환")
    @DeleteMapping("/{id}")
    public CommonResponse problemRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable Long id) {
        return problemService.delete(cudVO, id);
    }
}
