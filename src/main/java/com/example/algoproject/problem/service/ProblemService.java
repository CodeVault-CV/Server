package com.example.algoproject.problem.service;

import com.example.algoproject.errors.exception.NotExistProblemException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.problem.domain.Platform;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.problem.dto.request.AddProblem;
import com.example.algoproject.problem.dto.request.ProblemWeekList;
import com.example.algoproject.problem.dto.response.ProblemInfo;
import com.example.algoproject.problem.repository.ProblemRepository;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.service.SessionService;
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

    @Transactional
    public CommonResponse create(AddProblem request) {

        Session session = sessionService.findById(request.getSessionId());
        Problem problem = new Problem(request);

        problem.setSession(session);
        session.addProblem(problem);
        sessionService.save(session);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse detail(Long id) {
        return responseService.getSingleResponse(new ProblemInfo(findById(id)));
    }

    @Transactional
    public CommonResponse list(Long sessionId) {
        return responseService.getListResponse(getProblemInfos(sessionService.findById(sessionId).getProblems()));
    }

    @Transactional
    public CommonResponse delete(Long id) {
        problemRepository.delete(findById(id));
        return responseService.getSuccessResponse();
    }

    @Transactional
    public Problem findById(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(NotExistProblemException::new);
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
