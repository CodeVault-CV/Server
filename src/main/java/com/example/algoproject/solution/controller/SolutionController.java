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

    @Operation(summary="솔루션 생성", description="code와 message 반환")
    @PostMapping()
    public CommonResponse solutionAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody AddSolution solution) throws IOException {
        return solutionService.create(cudVO, solution);
    }

    @Operation(summary="솔루션 조회", description="제출한 솔루션 있으면 코드&리드미 파일 올라가 있는 s3 링크 반환. 없으면 null")
    @GetMapping("/{id}")
    public CommonResponse solutionDetail(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("id") Long solutionId) {
        return solutionService.detail(cudVO, solutionId);
    }

    @Operation(summary="팀원들의 솔루션 등록 여부 조회", description="해당 문제를 풀어야하는 팀원들의 목록과 솔루션 등록 여부 list를 반환.")
    @GetMapping("/list/{problemId}")
    public CommonResponse solutionList(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("problemId") Long problemId) {
        return solutionService.list(cudVO, problemId);
    }

    @Operation(summary="솔루션 업데이트", description="code와 message 반환")
    @PutMapping(value="/{id}")
    public CommonResponse update(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody UpdateSolution solution, @PathVariable("id") Long id) throws IOException {
        return solutionService.update(cudVO, id, solution);
    }

    @Operation(summary="솔루션 삭제", description="등록한 솔루션을 삭제")
    @DeleteMapping("/{id}")
    public CommonResponse solutionRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("id") Long id) {
        return solutionService.delete(cudVO, id);
    }

    @Operation(summary = "Push Webhook의 payload를 받는 API (Client에서는 사용 X)")
    @PostMapping("/push-webhook")
    public void pushWebhook(@RequestBody Map<String, Object> response) {
        solutionService.pushWebhook(response);
    }
}