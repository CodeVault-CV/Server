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
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.service.StudyService;
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
    private final StudyService studyService;
    private final ResponseService responseService;

    @Transactional
    public CommonResponse create(AddProblem request) {

        Study study = studyService.getStudy(request.getStudyId());
        Problem problem = new Problem(request.getNumber(), request.getName(), request.getUrl(), request.getPlatform(), request.getWeek(), request.getTypes());

        problem.setStudy(study);
        study.addProblem(problem);
        studyService.save(study);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse detail(Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(NotExistProblemException::new);

        return responseService.getSingleResponse(new ProblemInfo(problem.getNumber(), problem.getName(),
                problem.getUrl(), problem.getPlatform(), problem.getWeek(), problem.getTypes()));
    }

    @Transactional
    public CommonResponse list(String studyId) {
        Study study = studyService.getStudy(studyId);

        return responseService.getListResponse(getProblems(study));
    }

    @Transactional
    public CommonResponse weekList(ProblemWeekList request) {
        Study study = studyService.getStudy(request.getStudyId());

        return responseService.getListResponse(getWeekProblems(study, request.getWeek()));
    }

    @Transactional
    public CommonResponse delete(Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(NotExistProblemException::new);
        problemRepository.delete(problem);
        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse getPlatforms() {
        return responseService.getListResponse(Platform.getList());
    }

    @Transactional
    public Problem findById(Long id) {
        return problemRepository.findById(id).orElseThrow(NotExistProblemException::new);
    }

    //
    // private
    //

    private List<ProblemInfo> getProblems(Study study) {
        List<ProblemInfo> problems = new ArrayList<>();

        for (Problem problem : problemRepository.findByStudy(study))
            problems.add(new ProblemInfo(problem.getNumber(), problem.getName(), problem.getUrl(), problem.getPlatform(), problem.getWeek(), problem.getTypes()));

        return problems;
    }

    private List<ProblemInfo> getWeekProblems(Study study, int week) {
        List<ProblemInfo> problems = new ArrayList<>();

        for (Problem problem : problemRepository.findByStudyAndWeek(study, week))
            problems.add(new ProblemInfo(problem.getNumber(), problem.getName(), problem.getUrl(), problem.getPlatform(), problem.getWeek(), problem.getTypes()));

        return problems;
    }
}
