package com.example.algoproject.problem.service;

import com.example.algoproject.contain.domain.Contain;
import com.example.algoproject.contain.service.ContainService;
import com.example.algoproject.errors.exception.notfound.NotExistProblemException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.github.service.GithubService;
import com.example.algoproject.have.domain.Have;
import com.example.algoproject.have.service.HaveService;
import com.example.algoproject.problem.domain.Platform;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.problem.dto.request.DeleteProblem;
import com.example.algoproject.problem.dto.response.ProblemInfo;
import com.example.algoproject.problem.repository.ProblemRepository;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.service.SessionService;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.tag.domain.Tag;
import com.example.algoproject.tag.service.TagService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ResponseService responseService;
    private final SessionService sessionService;
    private final UserService userService;
    private final ContainService containService;
    private final TagService tagService;
    private final HaveService haveService;
    private final GithubService githubService;

    //TODO 크롤링을 구현하기전 임시로 파일을 통해 프로그래머스의 문제를 저장
    @Value("${jsonPath}")
    private String path;

    @Transactional
    public CommonResponse create(AddProblem request) {

        Session session = sessionService.findById(request.getSessionId());
        Problem problem = findById(request.getProblemId());

        containService.save(new Contain(session, problem));

        return responseService.getSingleResponse(new ProblemInfo(problem));
    }

    @Transactional(readOnly = true)
    public CommonResponse list(Long sessionId) {

        return responseService.getListResponse(
                containService.findBySession(
                        sessionService.findById(sessionId)).stream()
                        .map(Contain::getProblem)
                        .map(this::getProblemInfo)
                        .toList());
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long id, DeleteProblem request) {

        User user = userService.findById(cudVO.getUsername());
        Problem problem = findById(id);
        Session session = sessionService.findById(request.getSessionId());
        Study study = session.getStudy();

        containService.findBySessionAndProblem(session, problem).getSolutions()
                .forEach(solution -> removeGithubFile(user,study,solution));

        containService.deleteBySessionAndProblem(session, problem);

        return responseService.getSuccessResponse();
    }

    @Transactional(readOnly = true)
    public Problem findById(Long id) {
        return problemRepository.findById(id).orElseThrow(NotExistProblemException::new);
    }

    //
    // private
    //

    private ProblemInfo getProblemInfo(Problem problem) {
        return new ProblemInfo(problem);
    }

    private void removeGithubFile(User user, Study study, Solution solution) {
        String codeSHA = githubService.checkFileResponse(user, user, solution.getCodePath(), study.getRepositoryName());
        String readMeSHA = githubService.checkFileResponse(user, user, solution.getReadMePath(), study.getRepositoryName());

        githubService.deleteFileResponse(codeSHA, user, user, study.getRepositoryName(), solution.getCodePath(), "delete");
        githubService.deleteFileResponse(readMeSHA, user, user, study.getRepositoryName(), solution.getReadMePath(), "delete");
    }

    @Transactional
    public void getBoJTags() throws ExecutionException, InterruptedException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        Response response = client.prepare("GET", "https://solved.ac/api/v3/tag/list")
                .setHeader("Content-Type", "application/json")
                .execute()
                .toCompletableFuture()
                .get();

        ((List<Map<String, Object>>) mapper.readValue(response.getResponseBody(), Map.class).get("items")).stream()
                .map(this::mapToTag)
                .forEach(tagService::save);
    }

    @Transactional
    public void getBojProblems() throws ExecutionException, InterruptedException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        Response response;

        // 문제리스트를 가져와 데이터 베이스에 저장
        List<Map<String, Object>> problemList = new ArrayList<>();
        int page = 1;
        do {
            response = client.prepare("GET", "https://solved.ac/api/v3/search/problem")
                    .setHeader("Content-Type", "application/json")
                    .addQueryParam("query", "")
                    .addQueryParam("page", String.valueOf(page++))
                    .execute()
                    .toCompletableFuture()
                    .get();

            problemList.addAll((List<Map<String, Object>>) mapper.readValue(response.getResponseBody(), Map.class).get("items"));

            System.out.println("problem count: " + problemList.size());

            Thread.sleep(12000);
        } while ((int) mapper.readValue(response.getResponseBody(), Map.class).get("count") != problemList.size());

        problemList.stream()
                .map(map -> mapToProblem(Platform.boj, map))
                .forEach(map -> saveBoj((Problem) map.get("problem"), (List<Long>) map.get("tags")));
    }

    // TODO Programmers 사이트에서 전체 문제를 가져와 크롤링하는 메소드 추가
    @Transactional
    public void getProgrammersProblems() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ((List<Map<String, Object>>) mapper.readValue(new File(path), Map.class).get("programmers")).stream()
                .map(map -> (Problem) mapToProblem(Platform.programmers, map).get("problem"))
                .forEach(this::saveProgrammers);
    }

    private Tag mapToTag(Map<String, Object> map) {
        return new Tag(Long.valueOf(map.get("bojTagId").toString()), map.get("key").toString()
                , ((List<Map<String, Object>>) map.get("displayNames")).get(0).get("name").toString());
    }

    private Map<String, Object> mapToProblem(Platform platform, Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();

        if (platform.equals(Platform.boj)) {
            result.put("problem", new Problem(map.get("problemId").toString(), map.get("titleKo").toString(),
                    Integer.parseInt(map.get("level").toString()), Platform.boj));
            result.put("tags", ((List<Map<String, Object>>) map.get("tags")).stream()
                    .map(m -> Long.valueOf(m.get("bojTagId").toString()))
                    .toList());
        } else if (platform.equals(Platform.programmers)) {
            result.put("problem", new Problem(map.get("problemId").toString(), map.get("title").toString(),
                    Integer.parseInt(map.get("level").toString()), Platform.programmers));
        }
        return result;
    }

    private void saveBoj(Problem problem, List<Long> tags) {
        if (problemRepository.findByNumberAndPlatform(problem.getNumber(), problem.getPlatform()).isPresent())
            return;
        problemRepository.save(problem);
        tags.forEach(id -> haveService.save(new Have(problem, tagService.findById(id))));
    }

    private void saveProgrammers(Problem problem) {
        if (problemRepository.findByNumberAndPlatform(problem.getNumber(), problem.getPlatform()).isPresent())
            return;
        problemRepository.save(problem);
    }

    // TODO 문제를 주기적(밤 12시)으로 업데이트 하는 스케줄러 추가
}
