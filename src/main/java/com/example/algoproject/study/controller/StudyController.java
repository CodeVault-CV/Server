package com.example.algoproject.study.controller;

import com.example.algoproject.errors.SuccessResponse;
import com.example.algoproject.study.dto.AddMemberRequest;
import com.example.algoproject.study.dto.MemberInfoResponse;
import com.example.algoproject.study.dto.MemberListRequest;
import com.example.algoproject.study.dto.StudyInfoResponse;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/study")
@RestController
public class StudyController {

    private final StudyService studyService;

    @ApiOperation(value="스터디 생성", notes="studyId 반환")
    @PostMapping()
    public String studyAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestParam @Valid String name) {
        return studyService.create(cudVO, name);
    }

    @ApiOperation(value="멤버 추가", notes="code와 message 반환")
    @PostMapping("/member")
    public SuccessResponse memberAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid AddMemberRequest request) {
        return studyService.addMember(cudVO, request);
    }

    @ApiOperation(value="멤버 조회", notes="스터디에 참여중인 멤버 리스트 반환")
    @GetMapping("/member/list")
    public List<MemberInfoResponse> memberList(@AuthenticationPrincipal @RequestBody @Valid MemberListRequest request) {
        return studyService.getMembers(request);
    }

    @ApiOperation(value="스터디 조회", notes="스터디의 정보룰 반환(스터디 이름, 레포지토리 주소, 멤버들의 목록)")
    @GetMapping("/{studyId}")
    public StudyInfoResponse studyDetails(@AuthenticationPrincipal @PathVariable("studyId") String studyId) {
        return studyService.detail(studyId);
    }
}
