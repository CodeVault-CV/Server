package com.example.algoproject.contain.service;

import com.example.algoproject.contain.domain.Contain;
import com.example.algoproject.contain.repository.ContainRepository;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.session.domain.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContainService {

    private final ContainRepository containRepository;

    @Transactional
    public void save(Contain contain) {
        containRepository.save(contain);
    }

    @Transactional(readOnly = true)
    public List<Contain> findBySession(Session session) {
        return containRepository.findBySession(session);
    }

    @Transactional(readOnly = true)
    public Contain findBySessionAndProblem(Session session, Problem problem) {
        return containRepository.findBySessionAndProblem(session, problem);
    }

    @Transactional
    public void deleteAllBySession(Session session) {
        containRepository.deleteAllBySession(session);
    }

    @Transactional
    public void deleteBySessionAndProblem(Session session, Problem problem) {
        containRepository.deleteBySessionAndProblem(session, problem);
    }
}
