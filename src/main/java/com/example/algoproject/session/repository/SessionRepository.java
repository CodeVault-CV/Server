package com.example.algoproject.session.repository;

import com.example.algoproject.session.domain.Session;
import com.example.algoproject.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStudy(Study study);
}
