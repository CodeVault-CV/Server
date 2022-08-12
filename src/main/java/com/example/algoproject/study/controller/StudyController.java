package com.example.algoproject.study.controller;

import com.example.algoproject.errors.response.*;
import com.example.algoproject.study.dto.request.Member;
import com.example.algoproject.study.dto.request.CreateStudy;
import com.example.algoproject.study.dto.request.SearchUser;
import com.example.algoproject.study.dto.request.UpdateStudy;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/study")
@RestController
public class StudyController {

    private final StudyService studyService;

    @Operation(summary = "스터디 생성", description = "studyName(스터디 이름), repoName(Git 저장소 이름)을 받아 스터디 정보를 반환")
    @PostMapping()
    public CommonResponse studyAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid CreateStudy request) {
        return studyService.create(cudVO, request);
    }

    @Operation(summary = "스터디 수정(팀장만 가능)", description = "id(스터디 ID)와 name(새로 변경할 이름)을 받음")
    @PutMapping()
    public CommonResponse studyModify(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid UpdateStudy request) {
        return studyService.update(cudVO, request);
    }

    @Operation(summary = "스터디 조회", description = "id(스터디 ID)를 받아 스터디의 정보를 반환(스터디 ID, 스터디 이름, 레포지토리 주소, 팀장의 ID, 멤버 목록)")
    @GetMapping("/{id}")
    public CommonResponse studyDetail(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable String id) {
        return studyService.detail(cudVO, id);
    }

    @Operation(summary = "스터디 목록", description = "사용자가 참여중인 스터디 목록([스터디 ID, 이름] List)을 반환")
    @GetMapping("/list")
    public CommonResponse studyList(@AuthenticationPrincipal CustomUserDetailsVO cudVO) {
        return studyService.list(cudVO);
    }

    @Operation(summary = "스터디 삭제(팀장만 가능)", description = "id(스터디 ID)로 스터디 삭제 후 성공 여부만 반환")
    @DeleteMapping("/{id}")
    public CommonResponse studyRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable String id) {
        return studyService.delete(cudVO, id);
    }

    @Operation(summary = "멤버 추가(팀장만 가능)", description = "member(추가할 멤버의 이름)과 studyId(스터디 ID)를 받아 성공 여부만 반환")
    @PostMapping("/member")
    public CommonResponse memberAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid Member request) {
        return studyService.addMember(cudVO, request);
    }

    @Operation(summary = "멤버 삭제(팀장만 가능)", description = "member(삭제할 멤버의 이름)과 studyId(스터디 ID)를 받아 성공 여부만 반환")
    @DeleteMapping("/member")
    public CommonResponse memberRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid Member request) {
        return studyService.deleteMember(cudVO, request);
    }

    @Operation(summary = "멤버 초대를 위한 유저 검색(팀장만 가능)", description = "name(검색할 유저의 이름)을 받아 해당 이름으로 검색된 유저정보 리스트 반환")
    @GetMapping("/member/list")
    public CommonResponse memberSearch(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid SearchUser request) {
        return studyService.searchMember(cudVO, request);
    }

    @Operation(summary = "Repository Webhook의 payload를 받는 API (Client에서는 사용 X)")
    @PostMapping("/repository-webhook")
    public void repositoryWebhook(@RequestBody Map<String, Object> response) {
        studyService.repoWebhook(response);
    }

    @Operation(summary = "Member Webhook의 payload를 받는 API (Client에서는 사용 X)")
    @PostMapping("/member-webhook")
    public void memberWebhook(@RequestBody Map<String, Object> response) {
        studyService.memberWebhook(response);
    }
}
