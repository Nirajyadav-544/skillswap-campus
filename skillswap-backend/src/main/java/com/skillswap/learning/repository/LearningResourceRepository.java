package com.skillswap.learning.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.learning.entity.LearningResource;
import com.skillswap.learning.entity.LearningResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningResourceRepository
        extends JpaRepository<LearningResource, Long> {

    List<LearningResource> findByLearningPathId(
            Long learningPathId
    );

    Page<LearningResource> findByLearningPathId(
            Long learningPathId,
            Pageable pageable
    );

    List<LearningResource> findByLearningPathIdOrderByStepNumberAsc(
            Long learningPathId
    );

    List<LearningResource> findByUser(
            User user
    );

    Page<LearningResource> findByUser(
            User user,
            Pageable pageable
    );

    List<LearningResource> findByUserId(
            Long userId
    );

    Page<LearningResource> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<LearningResource> findByResourceType(
            LearningResourceType resourceType
    );

    Page<LearningResource> findByResourceType(
            LearningResourceType resourceType,
            Pageable pageable
    );

    List<LearningResource> findBySkillNameContainingIgnoreCase(
            String skillName
    );

    Page<LearningResource> findBySkillNameContainingIgnoreCase(
            String skillName,
            Pageable pageable
    );

    List<LearningResource> findByLearningPathIdAndCompletedFalse(
            Long learningPathId
    );

    List<LearningResource> findByLearningPathIdAndRequiredTrue(
            Long learningPathId
    );

    List<LearningResource> findByLearningPathIdAndRequiredTrueAndCompletedFalse(
            Long learningPathId
    );

    Optional<LearningResource> findByIdAndUser(
            Long id,
            User user
    );

    Optional<LearningResource> findByIdAndUserId(
            Long id,
            Long userId
    );

    Optional<LearningResource> findByIdAndLearningPathId(
            Long id,
            Long learningPathId
    );

    boolean existsByIdAndUser(
            Long id,
            User user
    );

    boolean existsByLearningPathIdAndTitleIgnoreCase(
            Long learningPathId,
            String title
    );

    long countByLearningPathId(
            Long learningPathId
    );

    long countByLearningPathIdAndCompletedTrue(
            Long learningPathId
    );

    long countByLearningPathIdAndCompletedFalse(
            Long learningPathId
    );

    long countByLearningPathIdAndRequiredTrue(
            Long learningPathId
    );

    long countByLearningPathIdAndRequiredTrueAndCompletedFalse(
            Long learningPathId
    );

    long countByUserId(
            Long userId
    );

    void deleteByLearningPathId(
            Long learningPathId
    );

    void deleteByUser(
            User user
    );
}