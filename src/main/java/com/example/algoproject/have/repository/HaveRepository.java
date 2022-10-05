package com.example.algoproject.have.repository;

import com.example.algoproject.have.domain.Have;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HaveRepository extends JpaRepository<Have, Long> {
}
