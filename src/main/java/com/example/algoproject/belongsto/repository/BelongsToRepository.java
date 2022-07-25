package com.example.algoproject.belongsto.repository;

import com.example.algoproject.belongsto.domain.BelongsTo;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BelongsToRepository extends JpaRepository<BelongsTo, Long> {
    List<BelongsTo> findByStudy(Study study);
    List<BelongsTo> findByMember(User user);
    void deleteByStudyAndMember(Study study, User user);
    void deleteAllByStudy(Study study);
}
