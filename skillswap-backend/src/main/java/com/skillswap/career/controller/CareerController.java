package com.skillswap.career.controller;

import com.skillswap.career.dto.CareerAnalysisResponse;
import com.skillswap.career.dto.CareerRecommendationResponse;
import com.skillswap.career.dto.CareerReadinessResponse;
import com.skillswap.career.service.CareerService;
import com.skillswap.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/career")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @GetMapping("/analysis")
    public ResponseEntity<ApiResponse<CareerAnalysisResponse>> getAnalysis(
            Authentication authentication
    ) {
        CareerAnalysisResponse response =
                careerService.getAnalysis(authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career analysis retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/readiness")
    public ResponseEntity<ApiResponse<CareerReadinessResponse>> getReadiness(
            Authentication authentication
    ) {
        CareerReadinessResponse response =
                careerService.getReadiness(authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career readiness retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<CareerRecommendationResponse>>
    getRecommendations(
            Authentication authentication
    ) {
        CareerRecommendationResponse response =
                careerService.getRecommendations(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Career recommendations retrieved successfully",
                        response
                )
        );
    }
}