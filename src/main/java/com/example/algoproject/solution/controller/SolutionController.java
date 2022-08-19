package com.example.algoproject.solution.controller;

import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.solution.dto.request.AddSolution;
import com.example.algoproject.solution.dto.request.UpdateSolution;
import com.example.algoproject.solution.service.SolutionService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.util.Auth;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.example.algoproject.util.Auth.Role.*;

@RequiredArgsConstructor
@RequestMapping("/api/solution")
@RestController
public class SolutionController {

    private final SolutionService solutionService;

    @Auth(role = MEMBER)
    @Operation(summary="솔루션 생성", description="문제ID, code, readme, language를 받아 생성하고 솔루션의 정보를 반환")
    @PostMapping()
    public CommonResponse solutionAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody AddSolution solution) throws IOException {
        return solutionService.create(cudVO, solution);
    }

    @Auth(role = MEMBER)
    @Operation(summary="솔루션 조회", description="제출한 솔루션 있으면 코드&리드미 파일 올라가 있는 s3 링크 반환. 없으면 null")
    @GetMapping("/{id}")
    public CommonResponse solutionDetail(@AuthenticationPrincipal @PathVariable("id") Long id) {
        return solutionService.detail(id);
    }

    @Auth(role = MEMBER)
    @Operation(summary="팀원들의 솔루션 등록 여부 조회", description="해당 문제를 풀어야하는 팀원들의 목록과 솔루션 등록 여부 list를 반환.")
    @GetMapping("/list/{problemId}")
    public CommonResponse solutionList(@AuthenticationPrincipal @PathVariable("problemId") Long problemId) {
        return solutionService.list(problemId);
    }

    @Auth(role = MEMBER)
    @Operation(summary="솔루션 업데이트", description="솔루션 수정 후 솔루션의 정보를 반환")
    @PutMapping(value="/{id}")
    public CommonResponse update(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody UpdateSolution solution, @PathVariable("id") Long id) throws IOException {
        return solutionService.update(cudVO, id, solution);
    }

    @Auth(role = MEMBER)
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