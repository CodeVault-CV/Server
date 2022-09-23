package com.example.algoproject.contain.repository;

import com.example.algoproject.contain.domain.Contain;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.session.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContainRepository extends JpaRepository<Contain, Long> {
    List<Contain> findBySession(Session session);

    void deleteAllBySession(Session session);

    Contain findBySessionAndProblem(Session session, Problem problem);

    void deleteBySessionAndProblem(Session session, Problem problem);
}
