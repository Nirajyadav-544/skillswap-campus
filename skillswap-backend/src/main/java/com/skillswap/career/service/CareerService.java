package com.skillswap.career.service;

import com.skillswap.career.dto.CareerAnalysisResponse;
import com.skillswap.career.dto.CareerRecommendationResponse;
import com.skillswap.career.dto.CareerReadinessResponse;
import com.skillswap.career.entity.CareerGoal;
import com.skillswap.career.entity.CareerGoalStatus;
import com.skillswap.career.entity.CareerRoadmap;
import com.skillswap.career.entity.CareerRoadmapItemStatus;
import com.skillswap.career.repository.CareerGoalRepository;
import com.skillswap.career.repository.CareerRoadmapItemRepository;
import com.skillswap.career.repository.CareerRoadmapRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.common.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CareerService {

    private final UserRepository userRepository;
    private final CareerGoalRepository careerGoalRepository;
    private final CareerRoadmapRepository careerRoadmapRepository;
    private final CareerRoadmapItemRepository careerRoadmapItemRepository;
    private final CareerReadinessService careerReadinessService;

    public CareerService(
            UserRepository userRepository,
            CareerGoalRepository careerGoalRepository,
            CareerRoadmapRepository careerRoadmapRepository,
            CareerRoadmapItemRepository careerRoadmapItemRepository,
            CareerReadinessService careerReadinessService
    ) {
        this.userRepository = userRepository;
        this.careerGoalRepository = careerGoalRepository;
        this.careerRoadmapRepository = careerRoadmapRepository;
        this.careerRoadmapItemRepository = careerRoadmapItemRepository;
        this.careerReadinessService = careerReadinessService;
    }

    public CareerAnalysisResponse getAnalysis(String userEmail) {

        User user = getUserByEmail(userEmail);

        List<CareerGoal> goals =
                careerGoalRepository.findByUserOrderByCreatedAtDesc(user);

        List<CareerRoadmap> roadmaps =
                careerRoadmapRepository.findByUserOrderByCreatedAtDesc(user);

        int totalGoals = goals.size();

        int activeGoals = (int) goals.stream()
                .filter(goal -> goal.getStatus() == CareerGoalStatus.ACTIVE)
                .count();

        int completedGoals = (int) goals.stream()
                .filter(goal -> goal.getStatus() == CareerGoalStatus.COMPLETED)
                .count();

        int totalItems = 0;
        int completedItems = 0;

        for (CareerRoadmap roadmap : roadmaps) {
            totalItems += careerRoadmapItemRepository
                    .findByRoadmapOrderBySequenceNumberAsc(roadmap)
                    .size();

            completedItems += careerRoadmapItemRepository
                    .countByRoadmapAndStatus(
                            roadmap,
                            CareerRoadmapItemStatus.COMPLETED
                    );
        }

        double progress = totalItems == 0
                ? 0.0
                : (completedItems * 100.0) / totalItems;

        CareerAnalysisResponse response =
                new CareerAnalysisResponse();

        response.setUserId(user.getId());
        response.setTotalGoals(totalGoals);
        response.setActiveGoals(activeGoals);
        response.setCompletedGoals(completedGoals);
        response.setTotalRoadmaps(roadmaps.size());
        response.setTotalRoadmapItems(totalItems);
        response.setCompletedRoadmapItems(completedItems);
        response.setRoadmapProgressPercentage(progress);

        String targetRole = goals.stream()
                .filter(goal ->
                        goal.getStatus() == CareerGoalStatus.ACTIVE
                                && goal.getTargetRole() != null
                                && !goal.getTargetRole().isBlank()
                )
                .map(CareerGoal::getTargetRole)
                .findFirst()
                .orElse(null);

        response.setTargetRole(targetRole);

        List<String> skillGaps = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        if (totalGoals == 0) {
            recommendations.add(
                    "Create a career goal to define your target career direction."
            );
        }

        if (roadmaps.isEmpty()) {
            recommendations.add(
                    "Create a career roadmap to organize your preparation."
            );
        }

        if (totalItems > completedItems) {
            recommendations.add(
                    "Continue completing your roadmap items."
            );
        }

        if (activeGoals > 0 && completedGoals == 0) {
            recommendations.add(
                    "Focus on completing your active career goals."
            );
        }

        if (totalGoals > 0 && roadmaps.isEmpty()) {
            skillGaps.add(
                    "Career roadmap planning"
            );
        }

        response.setSkillGaps(skillGaps);
        response.setRecommendations(recommendations);

        return response;
    }

    public CareerReadinessResponse getReadiness(String userEmail) {
        User user = getUserByEmail(userEmail);
        return careerReadinessService.calculateReadiness(user);
    }

    public CareerRecommendationResponse getRecommendations(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        List<CareerGoal> goals =
                careerGoalRepository.findByUserOrderByCreatedAtDesc(user);

        String targetRole = goals.stream()
                .filter(goal ->
                        goal.getStatus() == CareerGoalStatus.ACTIVE
                                && goal.getTargetRole() != null
                                && !goal.getTargetRole().isBlank()
                )
                .map(CareerGoal::getTargetRole)
                .findFirst()
                .orElse(null);

        List<String> recommendations = new ArrayList<>();

        if (goals.isEmpty()) {
            recommendations.add(
                    "Create your first career goal."
            );
        }

        if (targetRole == null) {
            recommendations.add(
                    "Define a target role for more focused career planning."
            );
        }

        List<CareerRoadmap> roadmaps =
                careerRoadmapRepository.findByUserOrderByCreatedAtDesc(user);

        if (roadmaps.isEmpty()) {
            recommendations.add(
                    "Create a roadmap based on your target role."
            );
        }

        for (CareerRoadmap roadmap : roadmaps) {
            long totalItems =
                    careerRoadmapItemRepository.countByRoadmap(roadmap);

            long completedItems =
                    careerRoadmapItemRepository.countByRoadmapAndStatus(
                            roadmap,
                            CareerRoadmapItemStatus.COMPLETED
                    );

            if (totalItems > completedItems) {
                recommendations.add(
                        "Continue working through your roadmap: "
                                + roadmap.getTitle()
                );
            }
        }

        return new CareerRecommendationResponse(
                user.getId(),
                targetRole,
                recommendations
        );
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Authenticated user not found"
                        )
                );
    }
}