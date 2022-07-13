package com.example.algoproject.problem.controller;

import com.example.algoproject.errors.response.*;
import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.problem.dto.request.ProblemWeekList;
import com.example.algoproject.problem.service.ProblemService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/problem")
@RestController
public class ProblemController {

    private final ProblemService problemService;

    @ApiOperation(value="문제 추가", notes="code 와 message 반환")
    @PostMapping()
    public CommonResponse problemAdd(@AuthenticationPrincipal @RequestBody @Valid AddProblem request) {
        return problemService.create(request);
    }

    @ApiOperation(value = "문제 보기", notes = "문제 json 형태로 반환")
    @GetMapping("/{problemId}")
    public CommonResponse problemDetail(@AuthenticationPrincipal @PathVariable Long problemId) {
        return problemService.detail(problemId);
    }

    @ApiOperation(value = "세션에 등록 된 모든 문제 보기", notes = "전체 문제 리스트 반환")
    @GetMapping("/list/{sessionId}")
    public CommonResponse problemList(@AuthenticationPrincipal @PathVariable Long sessionId) {
        return problemService.list(sessionId);
    }

    @ApiOperation(value = "문제 삭제", notes = "code 와 message 를 반환")
    @DeleteMapping("/{problemId}")
    public CommonResponse problemRemove(@AuthenticationPrincipal @PathVariable Long problemId) {
        return problemService.delete(problemId);
    }
}
