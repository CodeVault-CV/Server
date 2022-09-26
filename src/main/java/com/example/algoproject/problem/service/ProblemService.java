package com.example.algoproject.problem.service;

import com.example.algoproject.contain.domain.Contain;
import com.example.algoproject.contain.service.ContainService;
import com.example.algoproject.errors.exception.notfound.NotExistProblemException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.github.service.GithubService;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.problem.dto.request.DeleteProblem;
import com.example.algoproject.problem.dto.response.ProblemInfo;
import com.example.algoproject.problem.repository.ProblemRepository;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.service.SessionService;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ResponseService responseService;
    private final SessionService sessionService;
    private final UserService userService;
    private final ContainService containService;
    private final GithubService githubService;

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
}
