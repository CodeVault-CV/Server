package com.example.algoproject.solution.repository;

import com.example.algoproject.contain.domain.Contain;
import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.solution.domain.Solution;
import com.example.algoproject.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    Optional<Solution> findByCodePath(String codePath);

    Optional<Solution> findByReadMePath(String readMePath);

    Optional<Solution> findByUserAndContain(User user, Contain contain);
}