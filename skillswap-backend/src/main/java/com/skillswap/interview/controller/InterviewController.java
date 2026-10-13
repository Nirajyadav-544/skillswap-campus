package com.skillswap.interview.controller;

import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.common.response.ApiResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.interview.dto.CreateInterviewRequest;
import com.skillswap.interview.dto.InterviewResponse;
import com.skillswap.interview.dto.InterviewResultResponse;
import com.skillswap.interview.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final UserRepository userRepository;

    public InterviewController(
            InterviewService interviewService,
            UserRepository userRepository
    ) {
        this.interviewService = interviewService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewResponse>> createInterview(
            Authentication authentication,
            @Valid @RequestBody CreateInterviewRequest request
    ) {
        User user = getUser(authentication.getName());

        InterviewResponse response =
                interviewService.createInterview(user, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview created successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getInterviews(
            Authentication authentication
    ) {
        User user = getUser(authentication.getName());

        List<InterviewResponse> response =
                interviewService.getUserInterviews(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interviews retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<ApiResponse<InterviewResponse>> getInterview(
            Authentication authentication,
            @PathVariable Long interviewId
    ) {
        User user = getUser(authentication.getName());

        InterviewResponse response =
                interviewService.getInterview(interviewId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview retrieved successfully",
                        response
                )
        );
    }

    @PostMapping("/{interviewId}/start")
    public ResponseEntity<ApiResponse<InterviewResponse>> startInterview(
            Authentication authentication,
            @PathVariable Long interviewId
    ) {
        User user = getUser(authentication.getName());

        InterviewResponse response =
                interviewService.startInterview(interviewId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview started successfully",
                        response
                )
        );
    }

    @PostMapping("/{interviewId}/complete")
    public ResponseEntity<ApiResponse<InterviewResultResponse>> completeInterview(
            Authentication authentication,
            @PathVariable Long interviewId
    ) {
        User user = getUser(authentication.getName());

        InterviewResultResponse response =
                interviewService.completeInterview(interviewId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview completed successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{interviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteInterview(
            Authentication authentication,
            @PathVariable Long interviewId
    ) {
        User user = getUser(authentication.getName());

        interviewService.deleteInterview(interviewId, user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview deleted successfully"
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