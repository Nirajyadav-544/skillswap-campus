package com.skillswap.career.service;

import com.skillswap.career.dto.CareerRoadmapResponse;
import com.skillswap.career.entity.CareerGoal;
import com.skillswap.career.entity.CareerRoadmap;
import com.skillswap.career.entity.CareerRoadmapItem;
import com.skillswap.career.repository.CareerGoalRepository;
import com.skillswap.career.repository.CareerRoadmapRepository;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CareerRoadmapService {

    private final CareerRoadmapRepository careerRoadmapRepository;
    private final CareerGoalRepository careerGoalRepository;

    public CareerRoadmapService(
            CareerRoadmapRepository careerRoadmapRepository,
            CareerGoalRepository careerGoalRepository
    ) {
        this.careerRoadmapRepository = careerRoadmapRepository;
        this.careerGoalRepository = careerGoalRepository;
    }

    public CareerRoadmapResponse createRoadmap(
            User user,
            Long careerGoalId,
            String title,
            String description,
            String targetRole
    ) {
        CareerGoal careerGoal = null;

        if (careerGoalId != null) {
            careerGoal = careerGoalRepository
                    .findByIdAndUser(careerGoalId, user)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Career goal not found"
                            )
                    );
        }

        CareerRoadmap roadmap = new CareerRoadmap(
                user,
                careerGoal,
                title,
                description,
                targetRole
        );

        return CareerRoadmapResponse.from(
                careerRoadmapRepository.save(roadmap)
        );
    }

    @Transactional(readOnly = true)
    public List<CareerRoadmapResponse> getUserRoadmaps(
            User user
    ) {
        return careerRoadmapRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(CareerRoadmapResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CareerRoadmapResponse> getUserRoadmaps(
            User user,
            Pageable pageable
    ) {
        return careerRoadmapRepository
                .findByUserOrderByCreatedAtDesc(user, pageable)
                .map(CareerRoadmapResponse::from);
    }

    @Transactional(readOnly = true)
    public CareerRoadmapResponse getRoadmap(
            Long roadmapId,
            User user
    ) {
        CareerRoadmap roadmap = careerRoadmapRepository
                .findByIdAndUser(roadmapId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Career roadmap not found"
                        )
                );

        return CareerRoadmapResponse.from(roadmap);
    }

    public void addItem(
            Long roadmapId,
            User user,
            CareerRoadmapItem item
    ) {
        CareerRoadmap roadmap = careerRoadmapRepository
                .findByIdAndUser(roadmapId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Career roadmap not found"
                        )
                );

        roadmap.addItem(item);
        roadmap.recalculateProgress();

        careerRoadmapRepository.save(roadmap);
    }

    public void removeItem(
            Long roadmapId,
            User user,
            CareerRoadmapItem item
    ) {
        CareerRoadmap roadmap = careerRoadmapRepository
                .findByIdAndUser(roadmapId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Career roadmap not found"
                        )
                );

        roadmap.removeItem(item);
        roadmap.recalculateProgress();

        careerRoadmapRepository.save(roadmap);
    }

    public void recalculateProgress(
            Long roadmapId,
            User user
    ) {
        CareerRoadmap roadmap = careerRoadmapRepository
                .findByIdAndUser(roadmapId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Career roadmap not found"
                        )
                );

        roadmap.recalculateProgress();

        careerRoadmapRepository.save(roadmap);
    }

    public void deleteRoadmap(
            Long roadmapId,
            User user
    ) {
        CareerRoadmap roadmap = careerRoadmapRepository
                .findByIdAndUser(roadmapId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Career roadmap not found"
                        )
                );

        careerRoadmapRepository.delete(roadmap);
    }

    @Transactional(readOnly = true)
    public long countRoadmaps(User user) {
        return careerRoadmapRepository.countByUser(user);
    }
}