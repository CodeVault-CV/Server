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
