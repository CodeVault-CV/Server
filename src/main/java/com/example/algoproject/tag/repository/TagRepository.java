package com.example.algoproject.tag.repository;

import com.example.algoproject.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findById(Long id);
}
