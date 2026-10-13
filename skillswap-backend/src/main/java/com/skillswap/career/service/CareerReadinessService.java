package com.skillswap.career.service;

import com.skillswap.career.dto.CareerReadinessResponse;
import com.skillswap.career.entity.CareerGoalStatus;
import com.skillswap.career.entity.CareerRoadmap;
import com.skillswap.career.entity.CareerRoadmapItemStatus;
import com.skillswap.career.repository.CareerGoalRepository;
import com.skillswap.career.repository.CareerRoadmapItemRepository;
import com.skillswap.career.repository.CareerRoadmapRepository;
import com.skillswap.identity.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CareerReadinessService {

    private final CareerGoalRepository careerGoalRepository;
    private final CareerRoadmapRepository careerRoadmapRepository;
    private final CareerRoadmapItemRepository careerRoadmapItemRepository;

    public CareerReadinessService(
            CareerGoalRepository careerGoalRepository,
            CareerRoadmapRepository careerRoadmapRepository,
            CareerRoadmapItemRepository careerRoadmapItemRepository
    ) {
        this.careerGoalRepository = careerGoalRepository;
        this.careerRoadmapRepository = careerRoadmapRepository;
        this.careerRoadmapItemRepository = careerRoadmapItemRepository;
    }

    public CareerReadinessResponse calculateReadiness(User user) {

        long totalGoals =
                careerGoalRepository.countByUser(user);

        long activeGoals =
                careerGoalRepository.countByUserAndStatus(
                        user,
                        CareerGoalStatus.ACTIVE
                );

        long completedGoals =
                careerGoalRepository.countByUserAndStatus(
                        user,
                        CareerGoalStatus.COMPLETED
                );

        long totalRoadmaps =
                careerRoadmapRepository.countByUser(user);

        List<CareerRoadmap> roadmaps =
                careerRoadmapRepository
                        .findByUserOrderByCreatedAtDesc(user);

        long totalItems = 0;
        long completedItems = 0;

        for (CareerRoadmap roadmap : roadmaps) {
            totalItems += careerRoadmapItemRepository
                    .countByRoadmap(roadmap);

            completedItems += careerRoadmapItemRepository
                    .countByRoadmapAndStatus(
                            roadmap,
                            CareerRoadmapItemStatus.COMPLETED
                    );
        }

        int goalsScore = calculateGoalsScore(
                totalGoals,
                activeGoals,
                completedGoals
        );

        int roadmapScore = calculateRoadmapScore(
                totalRoadmaps,
                totalItems
        );

        int completionScore = calculateCompletionScore(
                totalItems,
                completedItems
        );

        int readinessPercentage =
                (goalsScore + roadmapScore + completionScore) / 3;

        CareerReadinessResponse response =
                new CareerReadinessResponse();

        response.setUserId(user.getId());
        response.setReadinessPercentage(readinessPercentage);
        response.setGoalsScore(goalsScore);
        response.setRoadmapScore(roadmapScore);
        response.setCompletionScore(completionScore);
        response.setReadinessLevel(
                determineReadinessLevel(readinessPercentage)
        );
        response.setSummary(
                buildSummary(
                        readinessPercentage,
                        totalGoals,
                        totalRoadmaps,
                        totalItems,
                        completedItems
                )
        );

        return response;
    }

    private int calculateGoalsScore(
            long totalGoals,
            long activeGoals,
            long completedGoals
    ) {
        if (totalGoals == 0) {
            return 0;
        }

        double activeRatio =
                (double) activeGoals / totalGoals;

        double completedRatio =
                (double) completedGoals / totalGoals;

        return (int) Math.min(
                100,
                Math.round(
                        (activeRatio * 60)
                                + (completedRatio * 40)
                )
        );
    }

    private int calculateRoadmapScore(
            long totalRoadmaps,
            long totalItems
    ) {
        if (totalRoadmaps == 0) {
            return 0;
        }

        int score = 50;

        if (totalItems > 0) {
            score += 50;
        }

        return Math.min(score, 100);
    }

    private int calculateCompletionScore(
            long totalItems,
            long completedItems
    ) {
        if (totalItems == 0) {
            return 0;
        }

        return (int) Math.round(
                ((double) completedItems / totalItems) * 100
        );
    }

    private String determineReadinessLevel(
            int readinessPercentage
    ) {
        if (readinessPercentage >= 80) {
            return "HIGH";
        }

        if (readinessPercentage >= 60) {
            return "GOOD";
        }

        if (readinessPercentage >= 40) {
            return "MODERATE";
        }

        if (readinessPercentage > 0) {
            return "STARTING";
        }

        return "NOT_STARTED";
    }

    private String buildSummary(
            int readinessPercentage,
            long totalGoals,
            long totalRoadmaps,
            long totalItems,
            long completedItems
    ) {
        if (totalGoals == 0) {
            return "Create a career goal to start building your career plan.";
        }

        if (totalRoadmaps == 0) {
            return "Create a career roadmap to structure your career preparation.";
        }

        if (totalItems > 0 && completedItems < totalItems) {
            return "Continue completing your roadmap items to improve career readiness.";
        }

        if (readinessPercentage >= 80) {
            return "Your career plan is well structured and actively progressing.";
        }

        return "Keep working on your career goals and roadmap to improve readiness.";
    }
}