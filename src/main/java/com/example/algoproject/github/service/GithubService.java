package com.example.algoproject.github.service;

import com.example.algoproject.errors.exception.badrequest.AlreadyExistMemberException;
import com.example.algoproject.errors.exception.badrequest.AlreadyExistRepositoryNameException;
import com.example.algoproject.errors.exception.notfound.NotExistRepositoryException;
import com.example.algoproject.errors.exception.unauthorized.FailedResponseException;
import com.example.algoproject.github.dto.request.CreateWebhook;
import com.example.algoproject.solution.dto.request.CommitFileRequest;
import com.example.algoproject.solution.dto.request.DeleteSolution;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.dto.request.AddContributor;
import com.example.algoproject.study.dto.request.CreateRepository;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.github.dto.request.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Service
public class GithubService {

    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret}")
    private String clientSecret;
    @Value("${baseUrl}")
    private String url;

    public String getAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("User-Agent", "api-test");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TokenDto> response = restTemplate.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                entity,
                TokenDto.class
        );

        // code가 유효하지 않을 때
        if(response.getBody().getAccess_token() == null)
            throw new FailedResponseException("code가 유효하지 않거나 파기되었습니다");

        return response.getBody().getAccess_token();
    }

    public Map<String, Object> getUserInfo(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + token);
        headers.add("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>(){});
            return response.getBody();
        } catch (RuntimeException ex) { // accessToken 이 유효하지 않을 때는 code에서 제대로 왔다면 거의 발생하지 않음
            throw new FailedResponseException("유효하지 않은 Access Token 입니다");
        }
    }

    public void createWebhook(User leader, String repo) {
        createWebhookResponse(leader, repo, this.url + "/study/repository-webhook", new String[]{"repository"});
        createWebhookResponse(leader, repo, this.url + "/study/member-webhook", new String[]{"member"});
        createWebhookResponse(leader, repo, this.url + "/solution/push-webhook", new String[]{"push"});
    }

    public Map<String, Object> createRepository(User leader, String repoName) {

        HttpHeaders headers = makeHeader(leader);
        CreateRepository request = new CreateRepository();
        request.setName(repoName);
        request.setAuto_init(true);
        HttpEntity<CreateRepository> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/user/repos",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody();
        } catch (RuntimeException ex) {
            throw new AlreadyExistRepositoryNameException();
        }
    }

    public void deleteRepository(User leader, Study study) {

        HttpHeaders headers = makeHeader(leader);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.exchange(
                    "https://api.github.com/repos/" + leader.getName() + "/" + study.getRepositoryName(),
                    HttpMethod.DELETE,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
        } catch (RuntimeException ex) {
            throw new NotExistRepositoryException();
        }
    }

    public void addContributor(User leader, User member, Study study) {

        HttpHeaders headers = makeHeader(leader);

        AddContributor request = new AddContributor();
        request.setPermission("admin");

        HttpEntity<AddContributor> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + study.getRepositoryName() + "/collaborators/" + member.getName(),
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });
        if(response.getStatusCodeValue() == 204)
            throw new AlreadyExistMemberException();
    }

    public void removeContributor(User leader, User member, Study study) {

        HttpHeaders headers = makeHeader(leader);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + study.getRepositoryName() + "/collaborators/" + member.getName(),
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                });
    }

    public String checkFileResponse(User leader, User user, String path, String repoName) {
        HttpHeaders headers = makeHeader(user);
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity<>(headers); // http entity에 header 담아줌
        try { // 깃허브에 파일 존재.
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/repos/" + leader.getName() + "/" + repoName + "/contents/" + path,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody().get("sha").toString();
        } catch (HttpClientErrorException e) { // 깃허브에 파일 존재 x. 새로 생성되는 파일인 경우 404 에러 뜸.
            return null;
        }
    }

    public void commitFileResponse(String sha, User leader, User user, String content, String fileName, String path, String repoName, String commitMessage){
        HttpHeaders headers = makeHeader(user);
        CommitFileRequest request = new CommitFileRequest();
        request.setMessage(commitMessage);
        request.setContent(Base64.getEncoder().encodeToString(content.getBytes())); // 내용 base64로 인코딩 해줘야됨 (필수)

        if (sha != null) { // 기존 파일 수정하는 거면 sha 바디에 추가해야됨
            request.setSha(sha);
        }

        HttpEntity<CommitFileRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + repoName + "/contents/" + path + fileName,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });
    }

    public void deleteFileResponse(String sha, User leader, User user, String repoName, String path, String commitMessage) {
        HttpHeaders headers = makeHeader(user);
        DeleteSolution request = new DeleteSolution();
        request.setMessage(commitMessage);

        if (sha != null)
            request.setSha(sha);

        HttpEntity<DeleteSolution> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + repoName + "/contents/" + path,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                });
    }

    //
    // private method
    //

    private HttpHeaders makeHeader(User owner) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + owner.getAccessToken());
        headers.add("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    private void createWebhookResponse(User leader, String repoName, String webhookUrl, String[] events) {
        HttpHeaders headers = makeHeader(leader);
        CreateWebhook request = new CreateWebhook(events);
        request.getConfig().setUrl(webhookUrl);
        HttpEntity<CreateWebhook> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + repoName + "/hooks",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                });
    }
}
