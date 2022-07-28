package com.example.algoproject.problem.service;

import com.example.algoproject.errors.exception.notfound.NotExistProblemException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.problem.dto.response.ProblemInfo;
import com.example.algoproject.problem.repository.ProblemRepository;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.service.SessionService;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, AddProblem request) {

        Session session = sessionService.findById(request.getSessionId());
        Problem problem = new Problem(request);

        // 유저가 팀장인지 확인
        studyService.checkLeader(userService.findById(cudVO.getUsername()), session.getStudy());

        problem.setSession(session);
        session.addProblem(problem);
        sessionService.save(session);

        return responseService.getSingleResponse(new ProblemInfo(problem));
    }

    @Transactional
    public CommonResponse list(CustomUserDetailsVO cudVO, Long sessionId) {

        Session session = sessionService.findById(sessionId);

        // 유저가 해당 스터디에 소속되어 있는지 확인
        studyService.checkAuth(userService.findById(cudVO.getUsername()), session.getStudy());

        return responseService.getListResponse(getProblemInfos(session.getProblems()));
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long id) {

        // 유저가 팀장인지 확인
        studyService.checkAuth(userService.findById(cudVO.getUsername()), findById(id).getSession().getStudy());

        problemRepository.delete(findById(id));
        return responseService.getSuccessResponse();
    }

    @Transactional
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
