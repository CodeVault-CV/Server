package com.example.algoproject.session.controller;

import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.session.dto.request.CreateSession;
import com.example.algoproject.session.dto.request.UpdateSession;
import com.example.algoproject.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/session")
@RestController
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "세션 생성", description = "code와 message 반환")
    @PostMapping()
    public CommonResponse sessionAdd(@AuthenticationPrincipal @Valid @RequestBody CreateSession request) {
        return sessionService.create(request);
    }

    @Operation(summary = "세션 목록", description = "스터디의 세션 목록을 반환")
    @GetMapping("/list/{studyId}")
    public CommonResponse sessionList(@AuthenticationPrincipal @PathVariable String studyId) {
        return sessionService.list(studyId);
    }

    @Operation(summary = "세션 조회", description = "세션의 정보를 반환")
    @GetMapping("/{sessionId}")
    public CommonResponse sessionDetail(@AuthenticationPrincipal @PathVariable Long sessionId) {
        return sessionService.detail(sessionId);
    }

    @Operation(summary = "세션 수정", description = "code와 message 반환")
    @PutMapping()
    public CommonResponse sessionModify(@AuthenticationPrincipal @Valid @RequestBody UpdateSession request) {
        return sessionService.update(request);
    }

    @Operation(summary = "세션 삭제", description = "code와 message 반환")
    @DeleteMapping("/{sessionId}")
    public CommonResponse sessionRemove(@AuthenticationPrincipal @PathVariable Long sessionId) {
        return sessionService.delete(sessionId);
    }
}
