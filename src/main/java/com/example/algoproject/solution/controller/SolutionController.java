package com.example.algoproject.solution.controller;

import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.solution.dto.request.AddSolution;
import com.example.algoproject.solution.dto.request.UpdateSolution;
import com.example.algoproject.solution.service.SolutionService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/solution")
@RestController
public class SolutionController {

    private final SolutionService solutionService;

    @Operation(summary="솔루션 업로드", description="code와 message 반환")
    @PostMapping(value="/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CommonResponse solutionAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestPart AddSolution solution) throws IOException {
        return solutionService.create(cudVO, solution);
    }

    @Operation(summary="솔루션 조회", description="제출한 솔루션 있으면 코드&리드미 파일 올라가 있는 s3 링크 반환. 없으면 null")
    @GetMapping("/{solutionId}")
    public CommonResponse solutionDetail(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("solutionId") Long solutionId) {
        return solutionService.detail(cudVO, solutionId);
    }

    @Operation(summary="팀원들의 솔루션 등록 여부 조회", description="해당 문제를 풀어야하는 팀원들의 목록과 솔루션 등록 여부 list를 반환.")
    @GetMapping("/list/{problemId}")
    public CommonResponse solutionList(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("problemId") Long problemId) {
        return solutionService.list(cudVO, problemId);
    }

    @Operation(summary="솔루션 업데이트", description="code와 message 반환")
    @PostMapping(value="/update/{solutionId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CommonResponse update(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestPart(value="solution") UpdateSolution solution, @PathVariable("solutionId") Long solutionId) throws IOException {
        return solutionService.update(cudVO, solutionId, solution);
    }

    @Operation(summary="솔루션 삭제", description="등록한 솔루션을 삭제")
    @DeleteMapping("/delete/{solutionId}")
    public CommonResponse solutionRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("solutionId") Long solutionId) {
        return solutionService.delete(cudVO, solutionId);
    }

    @Operation(summary = "Push Webhook의 payload를 받는 API (Client에서는 사용 X)")
    @PostMapping("/push-webhook")
    public void pushWebhook(@RequestBody Map<String, Object> response) {
        solutionService.pushWebhook(response);
    }
}