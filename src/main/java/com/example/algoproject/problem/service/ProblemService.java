package com.example.algoproject.problem.service;

import com.example.algoproject.errors.exception.notfound.NotExistProblemException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.github.service.GithubService;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.problem.dto.response.ProblemInfo;
import com.example.algoproject.problem.repository.ProblemRepository;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.service.SessionService;
import com.example.algoproject.solution.domain.Solution;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ResponseService responseService;
    private final SessionService sessionService;
    private final UserService userService;
    private final StudyService studyService;
    private final GithubService githubService;

    private final PathUtil pathUtil;

    @Transactional
    public CommonResponse create(AddProblem request) {

        Session session = sessionService.findById(request.getSessionId());
        Problem problem = new Problem(request);

        problem.setSession(session);
        session.addProblem(problem);

        return responseService.getSingleResponse(new ProblemInfo(problem));
    }

    @Transactional(readOnly = true)
    public CommonResponse list(Long sessionId) {
        return responseService.getListResponse(getProblemInfos(
                problemRepository.findBySession(sessionService.findById(sessionId))));
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long id) {

        User user = userService.findById(cudVO.getUsername());
        Problem problem = findById(id);
        Study study = studyService.findById(problem.getSession().getStudy().getId());

        // 유저가 팀장인지 확인
        studyService.checkLeader(user, findById(id).getSession().getStudy());

        // 관련 솔루션 삭제
        for (Solution solution: problem.getSolutions()) {
            String codeSHA = githubService.checkFileResponse(user, user, solution.getCodePath(), study.getRepositoryName());
            String readMeSHA = githubService.checkFileResponse(user, user, solution.getReadMePath(), study.getRepositoryName());

            githubService.deleteFileResponse(codeSHA, user, user, study.getRepositoryName(), solution.getCodePath(), "delete");
            githubService.deleteFileResponse(readMeSHA, user, user, study.getRepositoryName(), solution.getReadMePath(), "delete");
        }
        problemRepository.delete(problem);

        return responseService.getSuccessResponse();
    }

    @Transactional(readOnly = true)
    public Problem findById(Long id) {
        return problemRepository.findById(id).orElseThrow(NotExistProblemException::new);
    }

    //
    // private
    //

    private List<ProblemInfo> getProblemInfos(List<Problem> problems) {
        List<ProblemInfo> infos = new ArrayList<>();
        for (Problem problem : problems)
            infos.add(new ProblemInfo(problem));
        return infos;
    }
}
