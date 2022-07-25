package com.example.algoproject.solution.service;

import com.example.algoproject.belongsto.domain.BelongsTo;
import com.example.algoproject.belongsto.service.BelongsToService;
import com.example.algoproject.errors.exception.notfound.NotExistSolutionException;
import com.example.algoproject.errors.exception.badrequest.NotMySolutionException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.service.ProblemService;
import com.example.algoproject.s3.S3Uploader;
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
import com.example.algoproject.util.ReadMeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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
    private final S3Uploader s3Uploader;
    private final PathUtil pathUtil;
    private final ReadMeUtil readMeUtil;

    public CommonResponse create(CustomUserDetailsVO cudVO, AddSolution addSolution, MultipartFile code) throws IOException {

        User user = userService.findById(cudVO.getUsername());
        Problem problem = problemService.findById(addSolution.getProblemId());
        Study study = studyService.findById(problem.getSession().getStudy().getId());

        String gitHubPath = pathUtil.makeGitHubPath(problem, user.getName());
        String s3Path = pathUtil.makeS3Path(study.getRepositoryName(), problem, user.getName());

        log.info("github repository path : " + gitHubPath);
        log.info("s3 repository path : " + s3Path);

        long date = System.currentTimeMillis();
        int i = code.getOriginalFilename().lastIndexOf("."); // 코드파일 확장자 분리
        String language = "";
        if (i > 0) {
            language = code.getOriginalFilename().substring(i + 1);
        }

        /* readme file 생성 메소드 */
        MultipartFile readMe = readMeUtil.makeReadMe(addSolution.getHeader(), addSolution.getContent());

        /* github에 file commit */
        checkFileResponse(code, user, gitHubPath, study.getRepositoryName(), problem.getPlatform() + " [" + problem.getNumber() + "]" + problem.getName() + " By " + user.getName());
        checkFileResponse(readMe, user, gitHubPath, study.getRepositoryName(), problem.getPlatform() + " [" + problem.getNumber() + "]" + problem.getName() + " By " + user.getName());

        /* s3에 file upload */
        String codeUrl = s3Uploader.upload(code, s3Path);
        String readMeUrl = s3Uploader.upload(readMe, s3Path);

        /* local 리드미 삭제 */
        readMeUtil.removeReadMe(readMe);

        /* DB에 저장 */
        solutionRepository.save(new Solution(user, problem, codeUrl, readMeUrl, new Timestamp(date), addSolution.getTime(), addSolution.getMemory(), language));

        return responseService.getSuccessResponse();
    }

    public CommonResponse detail(CustomUserDetailsVO cudVO, Long solutionId) {

        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);

        return responseService.getSingleResponse(new SolutionInfo(solution.getId(), solution.getCodeUrl(), solution.getReadMeUrl(), solution.getDate(), solution.getTime(), solution.getMemory(), solution.getReviews()));

    }

    public CommonResponse list(CustomUserDetailsVO cudVO, Long problemId) {

        Problem problem = problemService.findById(problemId);
        Study study = studyService.findById(problem.getSession().getStudy().getId());
        List<BelongsTo> belongs =  belongsToService.findByStudy(study);
        List<Solution> solutions = solutionRepository.findByProblem(problem); // null일 수도 있음
        List<SolutionListInfo> list = new ArrayList<>();

        for (User member: getMemberList(belongs)) { // 현재 스터디의 팀원들 중에서, probelmId를 푼 팀원은 언어와 풀이여부 true 반환. 안 풀었으면 false 반환.
            System.out.println(member.getName());
            SolutionListInfo info = new SolutionListInfo(member.getName(), member.getImageUrl(),false, "none"); // language enum 타입이라 null 안됨.. 일단 nont으로 해둠

            for (Solution solution: solutions) {
                if (solution.getUser().equals(member)) {
                    info.setLanguage(solution.getLanguage());
                    info.setSolve(true);
                }
            }
            list.add(info);
        }
        return responseService.getListResponse(list);
    }

    public CommonResponse update(CustomUserDetailsVO cudVO, Long solutionId, UpdateSolution updateSolution, MultipartFile code) throws IOException {

        User user = userService.findById(cudVO.getUsername());
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);
        Problem problem = problemService.findById(updateSolution.getProblemId());
        Study study = studyService.findById(problem.getSession().getStudy().getId());

        String gitHubPath = pathUtil.makeGitHubPath(solution.getProblem(), user.getName());
        String s3Path = pathUtil.makeS3Path(study.getRepositoryName(), problem, user.getName());

        /* readme file 생성 메소드 */
        MultipartFile readMe = readMeUtil.makeReadMe(updateSolution.getHeader(), updateSolution.getContent());

        /* github에 file commit */
        checkFileResponse(code, user, gitHubPath, study.getRepositoryName(), problem.getPlatform() + " [" + problem.getNumber() + "]" + problem.getName() + " By " + user.getName());
        checkFileResponse(readMe, user, gitHubPath, study.getRepositoryName(), problem.getPlatform() + " [" + problem.getNumber() + "]" + problem.getName() + " By " + user.getName());

        /* s3에 file upload */
        String codeUrl = s3Uploader.upload(code, s3Path);
        String readMeUrl = s3Uploader.upload(readMe, s3Path);

        /* local 리드미 삭제 */
        readMeUtil.removeReadMe(readMe);

        /* 메모리/시간복잡도, 등록시간 update */
        solution.setMemory(updateSolution.getMemory());
        solution.setTime(updateSolution.getTime());
        solution.setDate(new Timestamp(System.currentTimeMillis()));
        solution.setReadMeUrl(readMeUrl);
        solution.setCodeUrl(codeUrl);
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

    private void checkFileResponse(MultipartFile multipartFile, User user, String path, String repoName, String commitMessage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + user.getAccessToken());

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity<>(headers); // http entity에 header 담아줌
        try { // 깃허브에 파일 존재.
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/repos/" + user.getName() + "/" + repoName + "/contents/" + path + multipartFile.getOriginalFilename(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            commitFileResponse(response.getBody().get("sha").toString(), multipartFile, user, path, repoName, commitMessage);
        } catch (HttpClientErrorException e) { // 깃허브에 파일 존재 x. 새로 생성되는 파일인 경우 404 에러 뜸.
            commitFileResponse(null, multipartFile, user, path, repoName, commitMessage);
        }
    }

    /* github file commit 메소드 */
    private Map<String, Object> commitFileResponse(String sha, MultipartFile multipartFile, User user, String path, String repoName, String commitMessage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + user.getAccessToken());

        CommitFileRequest request = new CommitFileRequest();
        request.setMessage(commitMessage);
        request.setContent(Base64.getEncoder().encodeToString(multipartFile.getBytes())); // 내용 base64로 인코딩 해줘야됨 (필수)

        if (sha != null) { // 기존 파일 수정하는 거면 sha 바디에 추가해야됨
            request.setSha(sha);
        }

        HttpEntity<CommitFileRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + user.getName() + "/" + repoName + "/contents/" + path + multipartFile.getOriginalFilename(),
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });
        log.info("github path : " + path + multipartFile.getOriginalFilename());

        return response.getBody();
    }

    private List<User> getMemberList(List<BelongsTo> belongs) {

        List<User> members = new ArrayList<>();

        for (BelongsTo belongsTo : belongs)
            members.add(belongsTo.getMember());

        return members;
    }

}