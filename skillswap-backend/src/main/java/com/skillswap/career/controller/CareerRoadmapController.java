package com.skillswap.career.controller;

import com.skillswap.career.dto.CareerRoadmapResponse;
import com.skillswap.career.service.CareerRoadmapService;
import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.common.response.ApiResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/career/roadmaps")
public class CareerRoadmapController {

    private final CareerRoadmapService careerRoadmapService;
    private final UserRepository userRepository;

    public CareerRoadmapController(
            CareerRoadmapService careerRoadmapService,
            UserRepository userRepository
    ) {
        this.careerRoadmapService = careerRoadmapService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CareerRoadmapResponse>> createRoadmap(
            Authentication authentication,
            @RequestParam(required = false) Long careerGoalId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String targetRole
    ) {
        User user = getUser(authentication.getName());

        CareerRoadmapResponse response =
                careerRoadmapService.createRoadmap(
                        user,
                        careerGoalId,
                        title,
                        description,
                        targetRole
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career roadmap created successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CareerRoadmapResponse>>> getRoadmaps(
            Authentication authentication
    ) {
        User user = getUser(authentication.getName());

        List<CareerRoadmapResponse> response =
                careerRoadmapService.getUserRoadmaps(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career roadmaps retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{roadmapId}")
    public ResponseEntity<ApiResponse<CareerRoadmapResponse>> getRoadmap(
            Authentication authentication,
            @PathVariable Long roadmapId
    ) {
        User user = getUser(authentication.getName());

        CareerRoadmapResponse response =
                careerRoadmapService.getRoadmap(
                        roadmapId,
                        user
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career roadmap retrieved successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{roadmapId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoadmap(
            Authentication authentication,
            @PathVariable Long roadmapId
    ) {
        User user = getUser(authentication.getName());

        careerRoadmapService.deleteRoadmap(
                roadmapId,
                user
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career roadmap deleted successfully"
                )
        );
    }

    @PostMapping("/{roadmapId}/recalculate")
    public ResponseEntity<ApiResponse<Void>> recalculateProgress(
            Authentication authentication,
            @PathVariable Long roadmapId
    ) {
        User user = getUser(authentication.getName());

        careerRoadmapService.recalculateProgress(
                roadmapId,
                user
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career roadmap progress recalculated successfully"
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