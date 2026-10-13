package com.skillswap.career.repository;

import com.skillswap.career.entity.CareerRoadmapItem;
import com.skillswap.career.entity.CareerRoadmapItemStatus;
import com.skillswap.career.entity.CareerRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerRoadmapItemRepository
        extends JpaRepository<CareerRoadmapItem, Long> {

    List<CareerRoadmapItem> findByRoadmapOrderBySequenceNumberAsc(
            CareerRoadmap roadmap
    );

    List<CareerRoadmapItem> findByRoadmapAndStatusOrderBySequenceNumberAsc(
            CareerRoadmap roadmap,
            CareerRoadmapItemStatus status
    );

    Optional<CareerRoadmapItem> findByIdAndRoadmap(
            Long id,
            CareerRoadmap roadmap
    );

    long countByRoadmap(
            CareerRoadmap roadmap
    );

    long countByRoadmapAndStatus(
            CareerRoadmap roadmap,
            CareerRoadmapItemStatus status
    );

    boolean existsByIdAndRoadmap(
            Long id,
            CareerRoadmap roadmap
    );
}