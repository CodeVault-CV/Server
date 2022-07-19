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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/solution")
@RestController
public class SolutionController {

    private final SolutionService solutionService;

    @Operation(summary="솔루션 조회", description="제출한 솔루션 있으면 코드&리드미 파일 올라가 있는 s3 링크 반환. 없으면 null")
    @GetMapping("/{solutionId}")
    public CommonResponse detail(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable("solutionId") Long solutionId) {
        return solutionService.detail(cudVO, solutionId);
    }

    @Operation(summary="솔루션 업로드", description="code와 message 반환")
    @PostMapping(value="/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CommonResponse upload(@AuthenticationPrincipal CustomUserDetailsVO cudVO,
                                 @RequestPart AddSolution solution, @RequestPart MultipartFile code) throws IOException {
        return solutionService.upload(cudVO, solution, code);
    }

    @Operation(summary="솔루션 업데이트", description="code와 message 반환")
    @PostMapping(value="/update/{solutionId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CommonResponse update(@AuthenticationPrincipal CustomUserDetailsVO cudVO,
                                 @RequestPart(value="code", required = false) MultipartFile code, @RequestPart(value="solution", required = false) UpdateSolution solution, @PathVariable("solutionId") Long solutionId) throws IOException {
        return solutionService.update(cudVO, solutionId, solution, code);
    }
}