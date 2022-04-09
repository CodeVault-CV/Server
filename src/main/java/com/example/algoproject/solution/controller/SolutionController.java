package com.example.algoproject.solution.controller;

import com.example.algoproject.errors.SuccessResponse;
import com.example.algoproject.s3.S3Uploader;
import com.example.algoproject.solution.dto.AddSolution;
import com.example.algoproject.solution.dto.S3UrlResponse;
import com.example.algoproject.solution.service.SolutionService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value="솔루션 조회", notes="제출한 솔루션 있으면 코드&리드미 파일 올라가 있는 s3 링크 반환. 없으면 null")
    @GetMapping("/disp")
    public S3UrlResponse displaySolution(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestParam("problemIo") Long problemId) throws IOException {
        return solutionService.getFileUrl(cudVO, problemId);
    }

    @ApiOperation(value="솔루션 업로드", notes="code와 message 반환")
    @PostMapping(value="/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessResponse upload(@AuthenticationPrincipal CustomUserDetailsVO cudVO,
                                  @RequestPart AddSolution solution, @RequestPart MultipartFile code) throws IOException {

        return solutionService.upload(cudVO, solution, code);
    }
}