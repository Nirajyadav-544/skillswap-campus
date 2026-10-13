package com.skillswap.learning.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.learning.entity.LearningProgress;
import com.skillswap.learning.entity.LearningProgressStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningProgressRepository
        extends JpaRepository<LearningProgress, Long> {

    List<LearningProgress> findByUser(
            User user
    );

    Page<LearningProgress> findByUser(
            User user,
            Pageable pageable
    );

    List<LearningProgress> findByUserId(
            Long userId
    );

    Page<LearningProgress> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<LearningProgress> findByLearningPathId(
            Long learningPathId
    );

    Page<LearningProgress> findByLearningPathId(
            Long learningPathId,
            Pageable pageable
    );

    List<LearningProgress> findByLearningResourceId(
            Long learningResourceId
    );

    Optional<LearningProgress> findByLearningResourceIdAndUserId(
            Long learningResourceId,
            Long userId
    );

    Optional<LearningProgress> findByLearningPathIdAndLearningResourceIdAndUserId(
            Long learningPathId,
            Long learningResourceId,
            Long userId
    );

    List<LearningProgress> findByUserIdAndStatus(
            Long userId,
            LearningProgressStatus status
    );

    Page<LearningProgress> findByUserIdAndStatus(
            Long userId,
            LearningProgressStatus status,
            Pageable pageable
    );

    List<LearningProgress> findByLearningPathIdAndStatus(
            Long learningPathId,
            LearningProgressStatus status
    );

    Page<LearningProgress> findByLearningPathIdAndStatus(
            Long learningPathId,
            LearningProgressStatus status,
            Pageable pageable
    );

    List<LearningProgress> findByUserIdAndLearningPathId(
            Long userId,
            Long learningPathId
    );

    boolean existsByLearningResourceIdAndUserId(
            Long learningResourceId,
            Long userId
    );

    boolean existsByLearningPathIdAndLearningResourceIdAndUserId(
            Long learningPathId,
            Long learningResourceId,
            Long userId
    );

    long countByUserId(
            Long userId
    );

    long countByUserIdAndStatus(
            Long userId,
            LearningProgressStatus status
    );

    long countByLearningPathId(
            Long learningPathId
    );

    long countByLearningPathIdAndStatus(
            Long learningPathId,
            LearningProgressStatus status
    );

    long countByLearningPathIdAndStatusNot(
            Long learningPathId,
            LearningProgressStatus status
    );

    void deleteByLearningPathId(
            Long learningPathId
    );

    void deleteByLearningResourceId(
            Long learningResourceId
    );

    void deleteByUser(
            User user
    );
}