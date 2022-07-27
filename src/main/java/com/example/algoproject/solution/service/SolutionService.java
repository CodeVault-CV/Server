package com.example.algoproject.solution.service;

import com.example.algoproject.belongsto.domain.BelongsTo;
import com.example.algoproject.belongsto.service.BelongsToService;
import com.example.algoproject.errors.exception.notfound.NotExistSolutionException;
import com.example.algoproject.errors.exception.badrequest.NotMySolutionException;
import com.example.algoproject.errors.exception.badrequest.AlreadyExistSolutionException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.service.ProblemService;
import com.example.algoproject.solution.domain.Language;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.solution.dto.request.AddSolution;
import com.example.algoproject.solution.dto.request.CommitFileRequest;
import com.example.algoproject.solution.dto.request.UpdateSolution;
import com.example.algoproject.solution.dto.response.SolutionInfo;
import com.example.algoproject.solution.dto.response.SolutionListInfo;
import com.example.algoproject.solution.repository.SolutionRepository;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
import com.example.algoproject.util.PathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final UserService userService;
    private final ProblemService problemService;
    private final StudyService studyService;
    private final BelongsToService belongsToService;

    private final ResponseService responseService;
    private final PathUtil pathUtil;

    public CommonResponse create(CustomUserDetailsVO cudVO, AddSolution addSolution) throws IOException {

        User user = userService.findById(cudVO.getUsername());
        Problem problem = problemService.findById(addSolution.getProblemId());
        Study study = studyService.findById(problem.getSession().getStudy().getId());
        Optional<Solution> alreadyExist = solutionRepository.findByProblemAndUser(problem, user);

        if (alreadyExist.isPresent()) // 이미 현재유저가 해당 문제에 솔루션 등록한 경우
            throw new AlreadyExistSolutionException();

        String gitHubPath = pathUtil.makeGitHubPath(problem, user.getName());
        log.info("github repository path : " + gitHubPath);

        long date = System.currentTimeMillis(); // 솔루션 등록한 시간 기록
        String commitMessage = pathUtil.makeCommitMessage(problem, user.getName()); // 커밋메세지 만듦
        String fileName = problem.getNumber() + "." + addSolution.getLanguage(); // ***이거 프론트에서 언어 어케 주는지에 따라 매핑 해줘야될듯....

        /* github에 file commit */
        checkFileResponse(user, addSolution.getCode(), fileName, commitMessage, gitHubPath, study.getRepositoryName()); // code
        checkFileResponse(user, addSolution.getReadMe(), "README.md", commitMessage, gitHubPath, study.getRepositoryName()); // readMe

        /* DB에 저장 */
        solutionRepository.save(new Solution(user, problem, addSolution.getCode(), addSolution.getReadMe(), new Timestamp(date), addSolution.getLanguage()));

        return responseService.getSuccessResponse();
    }

    public CommonResponse detail(CustomUserDetailsVO cudVO, Long solutionId) {

        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);

        return responseService.getSingleResponse(new SolutionInfo(solution.getCode(), solution.getReadMe(), solution.getDate(), solution.getReviews()));

    }

    public CommonResponse list(CustomUserDetailsVO cudVO, Long problemId) {

        Problem problem = problemService.findById(problemId);
        Study study = studyService.findById(problem.getSession().getStudy().getId());
        List<BelongsTo> belongs =  belongsToService.findByStudy(study);
        List<Solution> solutions = solutionRepository.findByProblem(problem); // null일 수도 있음
        List<SolutionListInfo> list = new ArrayList<>();

        for (User member: getMemberList(belongs)) { // 현재 스터디의 팀원들 중에서, probelmId를 푼 팀원은 언어와 풀이여부 true 반환. 안 풀었으면 false 반환.
            SolutionListInfo info = new SolutionListInfo(false, null, member.getName(), member.getImageUrl(), "none"); // language enum 타입이라 null 안됨.. 일단 nont으로 해둠

            for (Solution solution: solutions) {
                if (solution.getUser().equals(member)) {
                    info.setSolutionId(solution.getId());
                    info.setLanguage(solution.getLanguage());
                    info.setSolve(true);
                }
            }
            list.add(info);
        }
        return responseService.getListResponse(list);
    }

    public CommonResponse update(CustomUserDetailsVO cudVO, Long solutionId, UpdateSolution updateSolution) throws IOException {

        User user = userService.findById(cudVO.getUsername());
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);
        Problem problem = problemService.findById(updateSolution.getProblemId());
        Study study = studyService.findById(problem.getSession().getStudy().getId());

        String gitHubPath = pathUtil.makeGitHubPath(solution.getProblem(), user.getName());

        String commitMessage = pathUtil.makeCommitMessage(problem, user.getName()); // 커밋메세지 만듦
        String fileName = problem.getNumber() + "." + updateSolution.getLanguage(); // ***이거 프론트에서 언어 어케 주는지에 따라 매핑 해줘야될듯....

        /* github에 file commit */
        checkFileResponse(user, updateSolution.getCode(), fileName, commitMessage, gitHubPath, study.getRepositoryName());
        checkFileResponse(user, updateSolution.getReadMe(), "README.md", commitMessage, gitHubPath, study.getRepositoryName());

        solution.setDate(new Timestamp(System.currentTimeMillis()));
        solution.setCode(updateSolution.getCode());
        solution.setReadMe(updateSolution.getReadMe());
        solution.setLanguage(Language.valueOf(updateSolution.getLanguage()));
        solutionRepository.save(solution);

        return responseService.getSuccessResponse();
    }

    public CommonResponse delete(CustomUserDetailsVO cudVO, Long solutionId) {

        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);

        if (!cudVO.getUsername().equals(solution.getUser().getId())) // 내 솔루션 아니면 삭제 불가
            throw new NotMySolutionException();

        solutionRepository.delete(solution); // 솔루션 삭제

        return responseService.getSuccessResponse();
    }

    public Solution findById(Long solutionId) {
        return solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);
    }

    public void save(Solution solution) {
        solutionRepository.save(solution);
    }

    /*
    private method
    */

    private void checkFileResponse(User user, String content, String fileName, String commitMessage, String path, String repoName) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + user.getAccessToken());

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity<>(headers); // http entity에 header 담아줌
        try { // 깃허브에 파일 존재.
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/repos/" + user.getName() + "/" + repoName + "/contents/" + path + fileName,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            commitFileResponse(response.getBody().get("sha").toString(), user, content, fileName, path, repoName, commitMessage);
        } catch (HttpClientErrorException e) { // 깃허브에 파일 존재 x. 새로 생성되는 파일인 경우 404 에러 뜸.
            commitFileResponse(null, user, content, fileName, path, repoName, commitMessage);
        }
    }

    /* github file commit 메소드 */
    private Map<String, Object> commitFileResponse(String sha, User user, String content, String fileName, String path, String repoName, String commitMessage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + user.getAccessToken());

        CommitFileRequest request = new CommitFileRequest();
        request.setMessage(commitMessage);
        request.setContent(Base64.getEncoder().encodeToString(content.getBytes())); // 내용 base64로 인코딩 해줘야됨 (필수)

        if (sha != null) { // 기존 파일 수정하는 거면 sha 바디에 추가해야됨
            request.setSha(sha);
        }

        HttpEntity<CommitFileRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + user.getName() + "/" + repoName + "/contents/" + path + fileName,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });
        log.info("github path : " + path + fileName);

        return response.getBody();
    }

    private List<User> getMemberList(List<BelongsTo> belongs) {

        List<User> members = new ArrayList<>();

        for (BelongsTo belongsTo : belongs)
            members.add(belongsTo.getMember());

        return members;
    }

    private String mappedToExtension(String language) {

        String extension = "";
        switch(language) {
            case "cpp": extension = "cpp";
                break;
            case "java": extension =  "java";
                break;
            case "javascript": extension =  "js";
                break;
            case "kotlin": extension =  "kt";
                break;
            case "python": extension =  "py";
                break;
            case "swift": extension =  "swift";
                break;
            case "typescript": extension =  "ts";
                break;
            default: extension = "none";
                break;
        }
        return extension;
    }

}