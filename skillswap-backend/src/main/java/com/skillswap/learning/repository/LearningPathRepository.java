package com.skillswap.learning.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.learning.entity.LearningPath;
import com.skillswap.learning.entity.LearningPathStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByUser(User user);

    Page<LearningPath> findByUser(User user, Pageable pageable);

    List<LearningPath> findByUserAndStatus(
            User user,
            LearningPathStatus status
    );

    Page<LearningPath> findByUserAndStatus(
            User user,
            LearningPathStatus status,
            Pageable pageable
    );

    List<LearningPath> findByStatus(LearningPathStatus status);

    Page<LearningPath> findByStatus(
            LearningPathStatus status,
            Pageable pageable
    );

    List<LearningPath> findBySkillNameContainingIgnoreCase(
            String skillName
    );

    Page<LearningPath> findBySkillNameContainingIgnoreCase(
            String skillName,
            Pageable pageable
    );

    List<LearningPath> findByUserId(Long userId);

    Page<LearningPath> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<LearningPath> findByUserIdAndStatus(
            Long userId,
            LearningPathStatus status
    );

    Page<LearningPath> findByUserIdAndStatus(
            Long userId,
            LearningPathStatus status,
            Pageable pageable
    );

    Optional<LearningPath> findByIdAndUser(
            Long id,
            User user
    );

    Optional<LearningPath> findByIdAndUserId(
            Long id,
            Long userId
    );

    long countByUser(User user);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(
            Long userId,
            LearningPathStatus status
    );

    boolean existsByIdAndUser(
            Long id,
            User user
    );

    void deleteByUser(User user);
}