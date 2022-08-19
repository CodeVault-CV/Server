package com.example.algoproject.session.service;

import com.example.algoproject.errors.exception.notfound.NotExistSessionException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.github.service.GithubService;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.dto.request.CreateSession;
import com.example.algoproject.session.dto.request.UpdateSession;
import com.example.algoproject.session.dto.response.SessionInfo;
import com.example.algoproject.session.repository.SessionRepository;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.solution.dto.response.SolutionListInfo;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ResponseService responseService;
    private final StudyService studyService;

    private final GithubService githubService;

    @Transactional
    public CommonResponse create(CreateSession request) {

        Study study = studyService.findById(request.getStudyId());

        Session session = new Session(request);
        sessionRepository.save(session);
        study.addSession(session);
        return responseService.getSingleResponse(new SessionInfo(session));
    }

    @Transactional(readOnly = true)
    public CommonResponse list(String studyId) {

        Study study = studyService.findById(studyId);

        return responseService.getListResponse(getSessionInfos(sessionRepository.findByStudy(study)));
    }

    @Transactional(readOnly = true)
    public CommonResponse detail(Long id) {
        return responseService.getSingleResponse(new SessionInfo(findById(id)));
    }

    @Transactional
    public CommonResponse update(UpdateSession request, Long id) {

        Session session = findById(id);
        session.update(request);
        sessionRepository.save(session);
        return responseService.getSingleResponse(new SessionInfo(session));
    }

    @Transactional
    public CommonResponse delete(Long id) {

        Session session = findById(id);
        User user = userService.findById(cudVO.getUsername());
        Study study = session.getStudy();

        // 유저가 팀장인지 확인
        studyService.checkLeader(user, study);

        // 관련 솔루션 삭제
        for (Problem problem: session.getProblems()) {
            for (Solution solution: problem.getSolutions()) {
                String codeSHA = githubService.checkFileResponse(user, user, solution.getCodePath(), study.getRepositoryName());
                String readMeSHA = githubService.checkFileResponse(user, user, solution.getReadMePath(), study.getRepositoryName());

                githubService.deleteFileResponse(codeSHA, user, user, study.getRepositoryName(), solution.getCodePath(), "delete");
                githubService.deleteFileResponse(readMeSHA, user, user, study.getRepositoryName(), solution.getReadMePath(), "delete");
            }
        }

        sessionRepository.delete(session);
        return responseService.getSuccessResponse();
    }

    @Transactional(readOnly = true)
    public Session findById(Long id) {
        return sessionRepository.findById(id).orElseThrow(NotExistSessionException::new);
    }

    @Transactional
    public void save(Session session) {
        sessionRepository.save(session);
    }

    //
    // private method
    //
    private List<SessionInfo> getSessionInfos(List<Session> sessions) {
        List<SessionInfo> sessionInfos = new ArrayList<>();
        for (Session session : sessions)
            sessionInfos.add(new SessionInfo(session));
        return sessionInfos;
    }
}
