package com.example.algoproject.solution.service;

import com.example.algoproject.belongsto.domain.BelongsTo;
import com.example.algoproject.belongsto.service.BelongsToService;
import com.example.algoproject.errors.exception.badrequest.NotMatchProblemAndSolutionException;
import com.example.algoproject.errors.exception.notfound.NotExistSolutionException;
import com.example.algoproject.errors.exception.badrequest.NotMySolutionException;
import com.example.algoproject.errors.exception.badrequest.AlreadyExistSolutionException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.github.service.GithubService;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.service.ProblemService;
import com.example.algoproject.solution.domain.Language;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.solution.dto.request.AddSolution;
import com.example.algoproject.solution.dto.request.UpdateSolution;
import com.example.algoproject.solution.dto.response.SolutionDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final GithubService githubService;

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, AddSolution addSolution) throws IOException {

        User user = userService.findById(cudVO.getUsername());
        Problem problem = problemService.findById(addSolution.getProblemId());
        Study study = studyService.findById(problem.getSession().getStudy().getId());
        User leader = userService.findById(study.getLeaderId());
        Optional<Solution> alreadyExist = solutionRepository.findByProblemAndUser(problem, user);

        if (alreadyExist.isPresent()) // 이미 현재유저가 해당 문제에 솔루션 등록했는지 확인
            throw new AlreadyExistSolutionException();

        long date = System.currentTimeMillis(); // 솔루션 등록한 시간 기록
        Timestamp timestamp = new Timestamp(date);
        String path = pathUtil.makeGitHubPath(problem, user.getName());
        String fileName = problem.getNumber() + "." + mappedToExtension(addSolution.getLanguage()); // 문제 번호 + 프론트에서 주는 언어에 맞춰서 확장자 매핑해서 파일명 생성
        String codePath = path + fileName;
        String readMePath = path + "README.md";
        String commitMessage = pathUtil.makeCommitMessage(problem, user.getName()); // 커밋메세지 만듦

        log.info("github repository path : " + path);

        /* github에 file commit */
        String codeSHA = githubService.checkFileResponse(leader, user, fileName, path, study.getRepositoryName()); // code
        String readMeSHA = githubService.checkFileResponse(leader, user, "README.md", path, study.getRepositoryName()); // readMe

        githubService.commitFileResponse(codeSHA, leader, user, addSolution.getCode(), fileName, path, study.getRepositoryName(), commitMessage);
        githubService.commitFileResponse(readMeSHA, leader, user, addSolution.getReadMe(), "README.md", path, study.getRepositoryName(), commitMessage);

        /* DB에 저장 */
        Solution solution = new Solution(user, problem, addSolution.getCode(), addSolution.getReadMe(), timestamp, addSolution.getLanguage(), codePath, readMePath);
        solutionRepository.save(solution);

        return responseService.getSingleResponse(new SolutionInfo(solution, user));
    }

    @Transactional(readOnly = true)
    public CommonResponse detail(CustomUserDetailsVO cudVO, Long id) {

        User user = userService.findById(cudVO.getUsername());
        Solution solution = findById(id);


        return responseService.getSingleResponse(new SolutionInfo(solution, user));
    }

    @Transactional(readOnly = true)
    public CommonResponse list(Long problemId) {
        Problem problem = problemService.findById(problemId);
        Study study = studyService.findById(problem.getSession().getStudy().getId());
        List<BelongsTo> belongs =  belongsToService.findByStudy(study);
        List<Solution> solutions = solutionRepository.findByProblem(problem);
        List<SolutionListInfo> list = new ArrayList<>();

        for (User member: getMemberList(belongs)) { // 현재 스터디의 팀원들 중에서, probelmId를 푼 팀원은 언어와 풀이여부 true 반환. 안 풀었으면 false 반환.
            SolutionListInfo info = new SolutionListInfo(false, null, member.getId(), member.getName(), member.getImageUrl(), "none");

            for (Solution solution: solutions) {
                if (solution.getUser().equals(member)) {
                    info.setId(solution.getId());
                    info.setLanguage(solution.getLanguage());
                    info.setSolve(true);
                }
            }
            list.add(info);
        }
        return responseService.getListResponse(list);
    }

    @Transactional
    public CommonResponse update(CustomUserDetailsVO cudVO, Long solutionId, UpdateSolution updateSolution) throws IOException {

        User user = userService.findById(cudVO.getUsername());
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);
        Problem problem = problemService.findById(updateSolution.getProblemId());
        Study study = studyService.findById(problem.getSession().getStudy().getId());
        User leader = userService.findById(study.getLeaderId());

        checkMySolution(user, solution); // 내 솔루션인지 확인

        if (problem.getId() != solution.getProblem().getId()) // 요청한 solutionId가 속한 문제와 요청한 문제가 다른경우 확인
            throw new NotMatchProblemAndSolutionException();

        long date = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(date);
        String path = pathUtil.makeGitHubPath(solution.getProblem(), user.getName());
        String fileName = problem.getNumber() + "." + mappedToExtension(updateSolution.getLanguage()); // 파일명 생성
        String codePath = path + fileName;
        String commitMessage = pathUtil.makeCommitMessage(problem, user.getName()); // 커밋메세지 만듦


        /* github file commit */
        String codeSHA = githubService.checkFileResponse(leader, user, fileName, path, study.getRepositoryName()); // code
        String readMeSHA = githubService.checkFileResponse(leader, user, "README.md", path, study.getRepositoryName()); // readMe

        githubService.commitFileResponse(codeSHA, leader, user, updateSolution.getCode(), fileName, path, study.getRepositoryName(), commitMessage);
        githubService.commitFileResponse(readMeSHA, leader, user, updateSolution.getReadMe(), "README.md", path, study.getRepositoryName(), commitMessage);

        solution.setDate(timestamp);
        solution.setCode(updateSolution.getCode());
        solution.setReadMe(updateSolution.getReadMe());
        solution.setLanguage(Language.valueOf(updateSolution.getLanguage()));
        solution.setCodePath(codePath);
        solutionRepository.save(solution);

        return responseService.getSingleResponse(new SolutionDTO(solutionId, user.getId(), updateSolution.getCode(), updateSolution.getReadMe(), timestamp, updateSolution.getLanguage()));
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long solutionId) {

        User user = userService.findById(cudVO.getUsername());
        Solution solution = solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);
        Problem problem = solution.getProblem();
        Study study = studyService.findById(problem.getSession().getStudy().getId());
        User leader = userService.findById(study.getLeaderId());

        checkMySolution(user, solution); // 내 솔루션인지 확인

        solutionRepository.delete(solution); // 솔루션 DB 삭제

        String path = pathUtil.makeGitHubPath(solution.getProblem(), user.getName());
        String commitMessage = pathUtil.makeCommitMessage(problem, user.getName()); // 커밋메세지 만듦
        String fileName = problem.getNumber() + "." + mappedToExtension(solution.getLanguage().name()); // 파일명 생성

        String codeSHA = githubService.checkFileResponse(leader, user, fileName, path, study.getRepositoryName());
        String readMeSHA = githubService.checkFileResponse(leader, user, "README.md", path, study.getRepositoryName());
        // 솔루션 github 삭제
        githubService.deleteFileResponse(codeSHA, leader, user, study.getRepositoryName(), path, fileName, commitMessage);
        githubService.deleteFileResponse(readMeSHA, leader, user, study.getRepositoryName(), path, "README.md", commitMessage);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public void pushWebhook(Map<String, Object> response) {

        // solution
        if (response.containsKey("head_commit")) {
            Map<String, Object> pushMap = (Map<String, Object>) response.get("head_commit");

            List<String> removed = (List<String>) pushMap.get("removed");

            if (!removed.isEmpty()) { // 솔루션 삭제
                for (String path: removed) {
                    Solution solution = findByPath(path).get();

                    if (solution.getCodePath().equals(path)) { // 코드파일이 삭제됐음 -> 코드 blank
                        solution.setCode("");
                        solution.setCodePath("");
                    }
                    if (solution.getReadMePath().equals(path)) { // 리드미파일이 삭제됐음 -> 리드미 blank
                        solution.setReadMe("");
                        solution.setReadMePath("");
                    }
                    solutionRepository.save(solution);

                    if (solution.getCodePath().equals("") && solution.getReadMePath().equals("")) // 둘 다 삭제됐으면 해당 솔루션 DB에서 삭제
                        solutionRepository.delete(solution);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Solution findById(Long solutionId) {
        return solutionRepository.findById(solutionId).orElseThrow(NotExistSolutionException::new);
    }

    @Transactional(readOnly = true)
    public Optional<Solution> findByPath(String path) {
        Optional<Solution> solutionCode = solutionRepository.findByCodePath(path);
        Optional<Solution> solutionReadMe = solutionRepository.findByReadMePath(path);

        if (!solutionCode.isEmpty())
            return solutionCode;
        else
            return solutionReadMe;
    }

    public void save(Solution solution) {
        solutionRepository.save(solution);
    }

    public void checkMySolution(User user, Solution solution) {
        if (!user.getId().equals(solution.getUser().getId())) // 내 솔루션 아니면 삭제 불가
            throw new NotMySolutionException();
    }

    /*
    private method
    */



    private List<User> getMemberList(List<BelongsTo> belongs) {

        List<User> members = new ArrayList<>();

        for (BelongsTo belongsTo : belongs)
            members.add(belongsTo.getMember());

        return members;
    }

    private String mappedToExtension(String language) {

        String extension = switch (language) {
            case "cpp" -> "cpp";
            case "java" -> "java";
            case "javascript" -> "js";
            case "kotlin" -> "kt";
            case "python" -> "py";
            case "swift" -> "swift";
            case "typescript" -> "ts";
            default -> "none";
        };
        return extension;
    }

}