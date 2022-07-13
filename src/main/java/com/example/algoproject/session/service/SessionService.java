package com.example.algoproject.session.service;

import com.example.algoproject.errors.exception.NotExistSessionException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.session.dto.request.CreateSession;
import com.example.algoproject.session.dto.request.UpdateSession;
import com.example.algoproject.session.dto.response.SessionInfo;
import com.example.algoproject.session.repository.SessionRepository;
import com.example.algoproject.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ResponseService responseService;
    private final StudyService studyService;

    @Transactional
    public CommonResponse create(CreateSession request) {
        Session session = new Session(request);
        sessionRepository.save(session);
        studyService.getStudy(request.getStudyId()).addSession(session);
        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse list(String studyId) {
        return responseService.getListResponse(
                sessionRepository.findByStudy(studyService.getStudy(studyId)));
    }

    @Transactional
    public CommonResponse detail(Long id) {
        return responseService.getSingleResponse(new SessionInfo(findById(id)));
    }

    @Transactional
    public CommonResponse update(UpdateSession request) {
        Session session = findById(request.getId());
        session.update(request);
        sessionRepository.save(session);
        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse delete(Long id) {
        sessionRepository.delete(findById(id));
        return responseService.getSuccessResponse();
    }

    @Transactional
    public Session findById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(NotExistSessionException::new);
    }

    @Transactional
    public void save(Session session) {
        sessionRepository.save(session);
    }
}
