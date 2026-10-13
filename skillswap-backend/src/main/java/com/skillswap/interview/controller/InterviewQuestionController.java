package com.skillswap.interview.controller;

import com.skillswap.common.exception.UnauthorizedException;
import com.skillswap.common.response.ApiResponse;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.interview.dto.InterviewQuestionResponse;
import com.skillswap.interview.dto.SubmitInterviewAnswerRequest;
import com.skillswap.interview.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;
    private final UserRepository userRepository;

    public InterviewQuestionController(
            InterviewQuestionService interviewQuestionService,
            UserRepository userRepository
    ) {
        this.interviewQuestionService =
                interviewQuestionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{interviewId}/questions")
    public ResponseEntity<ApiResponse<InterviewQuestionResponse>> addQuestion(
            Authentication authentication,
            @PathVariable Long interviewId,
            @RequestParam String question,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String expectedAnswer
    ) {
        User user = getUser(authentication.getName());

        InterviewQuestionResponse response =
                interviewQuestionService.addQuestion(
                        interviewId,
                        user,
                        question,
                        questionType,
                        difficulty,
                        expectedAnswer
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview question added successfully",
                        response
                )
        );
    }

    @GetMapping("/{interviewId}/questions")
    public ResponseEntity<ApiResponse<List<InterviewQuestionResponse>>> getQuestions(
            Authentication authentication,
            @PathVariable Long interviewId
    ) {
        User user = getUser(authentication.getName());

        List<InterviewQuestionResponse> response =
                interviewQuestionService.getQuestions(
                        interviewId,
                        user
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview questions retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{interviewId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<InterviewQuestionResponse>> getQuestion(
            Authentication authentication,
            @PathVariable Long interviewId,
            @PathVariable Long questionId
    ) {
        User user = getUser(authentication.getName());

        InterviewQuestionResponse response =
                interviewQuestionService.getQuestion(
                        interviewId,
                        questionId,
                        user
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview question retrieved successfully",
                        response
                )
        );
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answer")
    public ResponseEntity<ApiResponse<InterviewQuestionResponse>> submitAnswer(
            Authentication authentication,
            @PathVariable Long interviewId,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitInterviewAnswerRequest request
    ) {
        User user = getUser(authentication.getName());

        InterviewQuestionResponse response =
                interviewQuestionService.submitAnswer(
                        interviewId,
                        questionId,
                        user,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview answer submitted successfully",
                        response
                )
        );
    }

    @PostMapping("/{interviewId}/questions/{questionId}/score")
    public ResponseEntity<ApiResponse<InterviewQuestionResponse>> scoreQuestion(
            Authentication authentication,
            @PathVariable Long interviewId,
            @PathVariable Long questionId,
            @RequestParam int score
    ) {
        User user = getUser(authentication.getName());

        InterviewQuestionResponse response =
                interviewQuestionService.scoreQuestion(
                        interviewId,
                        questionId,
                        user,
                        score
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview question scored successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{interviewId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            Authentication authentication,
            @PathVariable Long interviewId,
            @PathVariable Long questionId
    ) {
        User user = getUser(authentication.getName());

        interviewQuestionService.deleteQuestion(
                interviewId,
                questionId,
                user
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Interview question deleted successfully"
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