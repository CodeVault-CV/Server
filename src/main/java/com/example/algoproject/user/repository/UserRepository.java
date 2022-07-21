package com.example.algoproject.user.repository;

import com.example.algoproject.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(String userId);
    Optional<User> findByName(String name);
}
