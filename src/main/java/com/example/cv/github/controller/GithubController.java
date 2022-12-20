package com.example.cv.github.controller;

import com.example.cv.github.dto.RepositoriesDto;
import com.example.cv.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/link/old") // 기존 저장소 연동 -> 레포 목록 반환
    public void showRepositories(@RequestParam String accessToken, @RequestParam String name) { // github 닉네임 임시로 파라미터로 전달
        List<RepositoriesDto> repositories = githubService.getRepositories(accessToken, name);
        System.out.println(repositories);
    }

    @GetMapping("/link/old/{repository}") // 기존 저장소 연동 -> 선택한 레포에 솔루션 다 커밋 (레포 저장도 해야하나)
    public void linkRepository(@PathVariable String repository, @RequestParam String accessToken, @RequestParam String name) {
        // 지금 유저의 모든 파일에 대해 메소드 호출
        githubService.checkFileResponse("", repository, accessToken);
    }

    @GetMapping("/link/new") // 새로운 저장소 연동 -> 레포 생성, 생성한 레포에 솔루션 다 커밋
    public void createRepository(@RequestParam String repository, @RequestParam String accessToken, @RequestParam String name) {
        Map<String, Object> map = githubService.createRepository(repository, accessToken, name);
        githubService.checkFileResponse("", repository, accessToken);
    }

}
