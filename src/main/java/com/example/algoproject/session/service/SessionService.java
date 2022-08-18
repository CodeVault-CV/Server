package com.example.algoproject.session.service;

import com.example.algoproject.errors.exception.notfound.NotExistSessionException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.dto.request.CreateSession;
import com.example.algoproject.session.dto.request.UpdateSession;
import com.example.algoproject.session.dto.response.SessionInfo;
import com.example.algoproject.session.repository.SessionRepository;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.service.StudyService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
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
    private final UserService userService;
    private final StudyService studyService;

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, CreateSession request) {

        User user = userService.findById(cudVO.getUsername());
        Study study = studyService.findById(request.getStudyId());

        // 사용자가 팀장인지 확인
        studyService.checkLeader(user, study);

        Session session = new Session(request);
        sessionRepository.save(session);
        study.addSession(session);
        return responseService.getSingleResponse(new SessionInfo(session));
    }

    @Transactional(readOnly = true)
    public CommonResponse list(CustomUserDetailsVO cudVO, String studyId) {

        User user = userService.findById(cudVO.getUsername());
        Study study = studyService.findById(studyId);

        // 유저가 스터디에 속한 멤버인지 확인
        studyService.checkAuth(user, study);

        return responseService.getListResponse(getSessionInfos(sessionRepository.findByStudy(study)));
    }

    @Transactional(readOnly = true)
    public CommonResponse detail(CustomUserDetailsVO cudVO, Long id) {
        Session session = findById(id);
        User user = userService.findById(cudVO.getUsername());
        Study study = session.getStudy();

        // 유저가 스터디에 속한 멤버인지 확인
        studyService.checkAuth(user, study);

        return responseService.getSingleResponse(new SessionInfo(session));
    }

    @Transactional
    public CommonResponse update(CustomUserDetailsVO cudVO, UpdateSession request, Long id) {
        Session session = findById(id);
        User user = userService.findById(cudVO.getUsername());
        Study study = session.getStudy();

        // 유저가 팀장인지 확인
        studyService.checkLeader(user, study);

        session.update(request);
        sessionRepository.save(session);
        return responseService.getSingleResponse(new SessionInfo(session));
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, Long id) {

        Session session = findById(id);
        User user = userService.findById(cudVO.getUsername());
        Study study = session.getStudy();

        // 유저가 팀장인지 확인
        studyService.checkLeader(user, study);

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
