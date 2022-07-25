package com.example.algoproject.study.repository;

import com.example.algoproject.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, String> {
    Optional<Study> findById(String id);
}
