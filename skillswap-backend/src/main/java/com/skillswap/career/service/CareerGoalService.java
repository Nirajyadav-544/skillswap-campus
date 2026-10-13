package com.skillswap.career.service;

import com.skillswap.career.dto.CareerGoalResponse;
import com.skillswap.career.dto.CreateCareerGoalRequest;
import com.skillswap.career.dto.UpdateCareerGoalRequest;
import com.skillswap.career.entity.CareerGoal;
import com.skillswap.career.entity.CareerGoalStatus;
import com.skillswap.career.repository.CareerGoalRepository;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CareerGoalService {

    private final CareerGoalRepository careerGoalRepository;

    public CareerGoalService(CareerGoalRepository careerGoalRepository) {
        this.careerGoalRepository = careerGoalRepository;
    }

    public CareerGoalResponse createGoal(
            User user,
            CreateCareerGoalRequest request
    ) {
        CareerGoal goal = new CareerGoal(
                user,
                request.getGoalTitle(),
                request.getDescription(),
                request.getTargetRole(),
                request.getTargetCompany(),
                request.getTargetDate()
        );

        return CareerGoalResponse.from(
                careerGoalRepository.save(goal)
        );
    }

    @Transactional(readOnly = true)
    public List<CareerGoalResponse> getUserGoals(User user) {
        return careerGoalRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(CareerGoalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CareerGoalResponse> getUserGoals(
            User user,
            Pageable pageable
    ) {
        return careerGoalRepository
                .findByUserOrderByCreatedAtDesc(user, pageable)
                .map(CareerGoalResponse::from);
    }

    @Transactional(readOnly = true)
    public List<CareerGoalResponse> getUserGoalsByStatus(
            User user,
            CareerGoalStatus status
    ) {
        return careerGoalRepository
                .findByUserAndStatusOrderByCreatedAtDesc(user, status)
                .stream()
                .map(CareerGoalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CareerGoalResponse getGoal(
            Long goalId,
            User user
    ) {
        CareerGoal goal = careerGoalRepository
                .findByIdAndUser(goalId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException("Career goal not found")
                );

        return CareerGoalResponse.from(goal);
    }

    public CareerGoalResponse updateGoal(
            Long goalId,
            User user,
            UpdateCareerGoalRequest request
    ) {
        CareerGoal goal = careerGoalRepository
                .findByIdAndUser(goalId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException("Career goal not found")
                );

        if (request.getGoalTitle() != null) {
            goal.setGoalTitle(request.getGoalTitle());
        }

        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }

        if (request.getTargetRole() != null) {
            goal.setTargetRole(request.getTargetRole());
        }

        if (request.getTargetCompany() != null) {
            goal.setTargetCompany(request.getTargetCompany());
        }

        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        if (request.getStatus() != null) {
            goal.setStatus(request.getStatus());
        }

        return CareerGoalResponse.from(
                careerGoalRepository.save(goal)
        );
    }

    public void deleteGoal(
            Long goalId,
            User user
    ) {
        CareerGoal goal = careerGoalRepository
                .findByIdAndUser(goalId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException("Career goal not found")
                );

        careerGoalRepository.delete(goal);
    }

    @Transactional(readOnly = true)
    public long countGoals(User user) {
        return careerGoalRepository.countByUser(user);
    }

    @Transactional(readOnly = true)
    public long countGoalsByStatus(
            User user,
            CareerGoalStatus status
    ) {
        return careerGoalRepository.countByUserAndStatus(user, status);
    }
}