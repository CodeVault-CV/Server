package com.example.algoproject.problem.repository;

import com.example.algoproject.problem.domain.Platform;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.session.domain.Session;
import com.example.algoproject.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findByNumberAndPlatform(String number, Platform platform);
}
