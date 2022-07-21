package com.example.algoproject.solution.repository;

import com.example.algoproject.problem.domain.Problem;
import com.example.algoproject.solution.domain.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    List<Solution> findByProblem(Problem problem);
}