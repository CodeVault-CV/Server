package com.example.algoproject.study.controller;

import com.example.algoproject.errors.response.*;
import com.example.algoproject.study.dto.request.Member;
import com.example.algoproject.study.dto.request.CreateStudy;
import com.example.algoproject.study.dto.request.UpdateStudy;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.util.Auth;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static com.example.algoproject.util.Auth.Role.*;

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

    @Auth(role = LEADER)
    @Operation(summary = "스터디 수정(팀장만 가능)", description = "id(스터디 ID)와 name(새로 변경할 이름)을 받고 스터디의 정보를 반환")
    @PutMapping("/{id}")
    public CommonResponse studyModify(@AuthenticationPrincipal @RequestBody @Valid UpdateStudy request, @PathVariable String id) {
        return studyService.update(request, id);
    }

    @Auth(role = MEMBER)
    @Operation(summary = "스터디 조회", description = "id(스터디 ID)를 받아 스터디의 정보를 반환(스터디 ID, 스터디 이름, 레포지토리 주소, 팀장의 ID, 멤버 목록)")
    @GetMapping("/{id}")
    public CommonResponse studyDetail(@AuthenticationPrincipal @PathVariable String id) {
        return studyService.detail(id);
    }

    @Operation(summary = "스터디 목록", description = "사용자가 참여중인 스터디 목록([스터디 ID, 이름] List)을 반환")
    @GetMapping("/list")
    public CommonResponse studyList(@AuthenticationPrincipal CustomUserDetailsVO cudVO) {
        return studyService.list(cudVO);
    }

    @Auth(role = LEADER)
    @Operation(summary = "스터디 삭제(팀장만 가능)", description = "id(스터디 ID)로 스터디 삭제 후 성공 여부만 반환")
    @DeleteMapping("/{id}")
    public CommonResponse studyRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable String id) {
        return studyService.delete(cudVO, id);
    }

    @Auth(role = LEADER)
    @Operation(summary = "멤버 추가(팀장만 가능)", description = "member(추가할 멤버의 이름)과 studyId(스터디 ID)를 받아 성공 여부만 반환")
    @PostMapping("/member")
    public CommonResponse memberAdd(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid Member request) {
        return studyService.addMember(cudVO, request);
    }

    @Auth(role = LEADER)
    @Operation(summary = "멤버 삭제(팀장만 가능)", description = "member(삭제할 멤버의 이름)과 studyId(스터디 ID)를 받아 성공 여부만 반환")
    @DeleteMapping("/member")
    public CommonResponse memberRemove(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @RequestBody @Valid Member request) {
        return studyService.deleteMember(cudVO, request);
    }

    @Auth(role = LEADER)
    @Operation(summary = "멤버 초대를 위한 유저 검색(팀장만 가능)", description = "name(검색할 유저의 이름)을 받아 해당 이름으로 검색된 유저정보 리스트 반환")
    @GetMapping("/member/list")
    public CommonResponse memberSearch(@AuthenticationPrincipal @RequestParam String id, @RequestParam String name) {
        return studyService.searchMember(id, name);
    }

    @Operation(summary = "요청한 사용자가 팀장인지 확인", description = "id(스터디 ID)를 받아 boolean 반환")
    @GetMapping("/leader/{id}")
    public CommonResponse leaderAuth(@AuthenticationPrincipal CustomUserDetailsVO cudVO, @PathVariable String id) {
        return studyService.authLeader(cudVO, id);
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
