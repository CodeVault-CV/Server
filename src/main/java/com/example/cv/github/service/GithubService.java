package com.example.cv.github.service;

import com.example.cv.github.dto.CommitDto;
import com.example.cv.github.dto.CreateRepositoryDto;
import com.example.cv.github.dto.RepositoriesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubService {

    public List<RepositoriesDto> getRepositories(String accessToken, String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github+json");
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<RepositoriesDto>> response = restTemplate.exchange(
                "https://api.github.com/users/" + name + "/repos",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );
        List<RepositoriesDto> list = response.getBody();

        return list;
    }

    public String checkFileResponse(String path, String repository, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + accessToken);
        headers.add("Accept", "application/vnd.github.v3+json");
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity<>(headers);
        try { // 깃허브에 파일 존재하는 경우
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/repos/" + "Johoseong" + "/" + repository + "/contents/" + path,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody().get("sha").toString();
        } catch (HttpClientErrorException e) { // 깃허브에 파일 존재 x. 새로 생성되는 파일인 경우 404 에러 뜸.
            return null;
        }
    }

    public void commitFileResponse(String sha, String content, String path, String repository, String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + accessToken);
        headers.add("Accept", "application/vnd.github.v3+json");
        CommitDto request = new CommitDto();
        request.setMessage(""); // 커밋 메세지
        request.setContent(Base64.getEncoder().encodeToString(content.getBytes())); // 내용 base64로 인코딩 해줘야됨 (필수)

        if (sha != null) { // 기존 파일 수정하는 거면 sha 바디에 추가해야됨
            request.setSha(sha);
        }

        HttpEntity<CommitDto> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + "Johoseong" + "/" + repository + "/contents/" + path,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> createRepository(String accessToken, String repository, String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + accessToken);
        headers.add("Accept", "application/vnd.github.v3+json");

        CreateRepositoryDto request = new CreateRepositoryDto();
        request.setName(repository);
        request.setAuto_init(true);
        HttpEntity<CreateRepositoryDto> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
