package com.skillswap.career.repository;

import com.skillswap.career.entity.CareerRoadmap;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerRoadmapRepository
        extends JpaRepository<CareerRoadmap, Long> {

    List<CareerRoadmap> findByUserOrderByCreatedAtDesc(
            User user
    );

    Page<CareerRoadmap> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );

    Optional<CareerRoadmap> findByIdAndUser(
            Long id,
            User user
    );

    List<CareerRoadmap> findByCareerGoalIdOrderByCreatedAtDesc(
            Long careerGoalId
    );

    Optional<CareerRoadmap> findFirstByUserOrderByCreatedAtDesc(
            User user
    );

    long countByUser(User user);

    boolean existsByIdAndUser(
            Long id,
            User user
    );
}