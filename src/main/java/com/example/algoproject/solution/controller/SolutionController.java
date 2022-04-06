package com.example.algoproject.solution.controller;

import com.example.algoproject.s3.S3Uploader;
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

    private final S3Uploader s3Uploader;
    private final SolutionService solutionService;

    //@RequestParam("problem_no") String problemNo, @RequestParam("code") MultipartFile multipartFile
    @ApiOperation(value="코드파일 업로드", notes="")
    @PostMapping("/code/upload")
    public String uploadCode(@AuthenticationPrincipal CustomUserDetailsVO cudVO,
                             @RequestParam("code") MultipartFile multipartFile,
                             @RequestParam("header") String header, @RequestParam("content") String content,
                             @RequestParam("time") String time, @RequestParam("memory") String memory) throws IOException {
        solutionService.upload(cudVO, multipartFile, header, content, time, memory);

        return "success";
    }

//    @ApiOperation(value="코드파일 조회", notes="")
//    @GetMapping("/code")
//    public String displayCode(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestParam("problem_no") String problemNo) throws IOException {
//        solutionService.getCodeFile(cudVO, problemNo);
//        return "test2";
//    }

//    @ApiOperation(value="리드미 조회", notes="")
//    @PostMapping("/readme")
//    public String dispReadMe(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestParam("problem_no") Long problemNo) {
//
//    }
}
