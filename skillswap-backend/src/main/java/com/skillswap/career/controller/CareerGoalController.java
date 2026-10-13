package com.skillswap.career.controller;

import com.skillswap.career.dto.CareerGoalResponse;
import com.skillswap.career.dto.CreateCareerGoalRequest;
import com.skillswap.career.dto.UpdateCareerGoalRequest;
import com.skillswap.career.entity.CareerGoalStatus;
import com.skillswap.career.service.CareerGoalService;
import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.common.response.ApiResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/career/goals")
public class CareerGoalController {

    private final CareerGoalService careerGoalService;
    private final UserRepository userRepository;

    public CareerGoalController(
            CareerGoalService careerGoalService,
            UserRepository userRepository
    ) {
        this.careerGoalService = careerGoalService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CareerGoalResponse>> createGoal(
            Authentication authentication,
            @Valid @RequestBody CreateCareerGoalRequest request
    ) {
        User user = getUser(authentication.getName());

        CareerGoalResponse response =
                careerGoalService.createGoal(user, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career goal created successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CareerGoalResponse>>> getGoals(
            Authentication authentication
    ) {
        User user = getUser(authentication.getName());

        List<CareerGoalResponse> response =
                careerGoalService.getUserGoals(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career goals retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<CareerGoalResponse>>> getGoalsPage(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        User user = getUser(authentication.getName());

        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<CareerGoalResponse> response =
                careerGoalService.getUserGoals(user, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career goals retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CareerGoalResponse>>>
    getGoalsByStatus(
            Authentication authentication,
            @PathVariable CareerGoalStatus status
    ) {
        User user = getUser(authentication.getName());

        List<CareerGoalResponse> response =
                careerGoalService.getUserGoalsByStatus(user, status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career goals retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<CareerGoalResponse>> getGoal(
            Authentication authentication,
            @PathVariable Long goalId
    ) {
        User user = getUser(authentication.getName());

        CareerGoalResponse response =
                careerGoalService.getGoal(goalId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career goal retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<ApiResponse<CareerGoalResponse>> updateGoal(
            Authentication authentication,
            @PathVariable Long goalId,
            @Valid @RequestBody UpdateCareerGoalRequest request
    ) {
        User user = getUser(authentication.getName());

        CareerGoalResponse response =
                careerGoalService.updateGoal(
                        goalId,
                        user,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career goal updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            Authentication authentication,
            @PathVariable Long goalId
    ) {
        User user = getUser(authentication.getName());

        careerGoalService.deleteGoal(goalId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career goal deleted successfully"
                )
        );
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Authenticated user not found"
                        )
                );
    }
}