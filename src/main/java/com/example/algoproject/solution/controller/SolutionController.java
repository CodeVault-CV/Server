package com.example.algoproject.solution.controller;

import com.example.algoproject.errors.SuccessResponse;
import com.example.algoproject.s3.S3Uploader;
import com.example.algoproject.solution.dto.S3UrlResponse;
import com.example.algoproject.solution.service.SolutionService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/solution")
@RestController
public class SolutionController {

    private final SolutionService solutionService;

    @ApiOperation(value="솔루션 조회", notes="제출한 풀이 있으면 풀이 s3 링크 반환. 없으면 null")
    @GetMapping("/disp")
    public S3UrlResponse dispProblem(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestParam("problemNo") String problemNo) throws IOException {
        return solutionService.getFileUrl(cudVO, problemNo);
    }

    //@RequestParam("problemNo") String problemNo
    @ApiOperation(value="솔루션 업로드", notes="code와 message 반환")
    @PostMapping("/upload")
    public SuccessResponse uploadCode(@AuthenticationPrincipal CustomUserDetailsVO cudVO,
                                      @RequestParam("code") MultipartFile multipartFile,
                                      @RequestParam("header") String header, @RequestParam("content") String content,
                                      @RequestParam("time") String time, @RequestParam("memory") String memory) throws IOException {

        return solutionService.upload(cudVO, multipartFile, header, content, time, memory);
    }
}