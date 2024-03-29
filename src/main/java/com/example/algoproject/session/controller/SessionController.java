package com.example.algoproject.session.controller;

import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.session.dto.request.CreateSession;
import com.example.algoproject.session.dto.request.UpdateSession;
import com.example.algoproject.session.service.SessionService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.util.Auth;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.algoproject.util.Auth.Role.*;

@RequiredArgsConstructor
@RequestMapping("/api/session")
@RestController
public class SessionController {

    private final SessionService sessionService;

    @Auth(role = LEADER)
    @Operation(summary = "세션 생성(팀장만 가능)", description = "스터디의 ID, 세션의 이름, 시작 날짜, 끝 날짜를 받아 생성된 세션의 정보를 반환")
    @PostMapping()
    public CommonResponse sessionAdd(@AuthenticationPrincipal @Valid @RequestBody CreateSession request) {
        return sessionService.create(request);
    }

    @Auth(role = LEADER)
    @Operation(summary = "세션 수정(팀장만 가능)", description = "변경된 세션의 이름, 시작 날짜, 끝 날짜를 받아 세션의 정보를 반환")
    @PutMapping("/{id}")
    public CommonResponse sessionModify(@AuthenticationPrincipal @Valid @RequestBody UpdateSession request, @PathVariable Long id) {
        return sessionService.update(request, id);
    }

    @Auth(role = MEMBER)
    @Operation(summary = "세션 목록", description = "스터디의 세션 정보 목록을 반환")
    @GetMapping("/list/{studyId}")
    public CommonResponse sessionList(@AuthenticationPrincipal @PathVariable String studyId) {
        return sessionService.list(studyId);
    }

    @Auth(role = MEMBER)
    @Operation(summary = "세션 조회", description = "세션의 정보(ID, 이름, 시작 날짜, 끝 날짜)를 반환")
    @GetMapping("/{id}")
    public CommonResponse sessionDetail(@AuthenticationPrincipal @PathVariable Long id) {
        return sessionService.detail(id);
    }

    @Auth(role = LEADER)
    @Operation(summary = "세션 삭제(팀장만 가능)", description = "세션 ID를 받아 성공 여부만 반환")
    @DeleteMapping("/{id}")
    public CommonResponse sessionRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable Long id) {
        return sessionService.delete(cudVO, id);
    }
}
