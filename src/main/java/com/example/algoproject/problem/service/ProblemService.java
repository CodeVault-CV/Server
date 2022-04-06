package com.example.algoproject.problem.service;

import com.example.algoproject.errors.exception.NotExistProblemException;
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

    @Transactional
    public void create(AddProblem request) {

        Study study = studyService.getStydy(request.getStudyId());
        Problem problem = new Problem(request.getNumber(), request.getName(), request.getUrl(), request.getPlatform(), request.getWeek(), request.getTypes());

        problem.setStudy(study);
        study.addProblem(problem);
        studyService.save(study);
    }

    @Transactional
    public ProblemInfo detail(Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(NotExistProblemException::new);

        return new ProblemInfo(problem.getNumber(), problem.getName(), problem.getUrl(), problem.getPlatform(), problem.getWeek(), problem.getTypes());
    }

    @Transactional
    public List<ProblemInfo> list(String studyId) {
        Study study = studyService.getStydy(studyId);

        return getProblems(study);
    }

    @Transactional
    public List<ProblemInfo> weekList(ProblemWeekList request) {
        Study study = studyService.getStydy(request.getStudyId());

        return getWeekProblems(study, request.getWeek());
    }

    @Transactional
    public void delete(Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(NotExistProblemException::new);
        problemRepository.delete(problem);
    }

    @Transactional
    public String[] getPlatforms() {
        return Platform.getList();
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
