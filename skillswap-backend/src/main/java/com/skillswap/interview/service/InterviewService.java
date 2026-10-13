package com.skillswap.interview.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.interview.dto.InterviewQuestionResponse;
import com.skillswap.interview.dto.InterviewResultResponse;
import com.skillswap.interview.dto.InterviewResponse;
import com.skillswap.interview.dto.CreateInterviewRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class InterviewService {

    private final AtomicLong interviewIdGenerator =
            new AtomicLong(1);

    private final Map<Long, InterviewData> interviews =
            new ConcurrentHashMap<>();

    public InterviewResponse createInterview(
            User user,
            CreateInterviewRequest request
    ) {
        Long interviewId =
                interviewIdGenerator.getAndIncrement();

        InterviewData interview =
                new InterviewData();

        interview.id = interviewId;
        interview.userId = user.getId();
        interview.title = request.getTitle();
        interview.description = request.getDescription();
        interview.targetRole = request.getTargetRole();
        interview.interviewType = request.getInterviewType();
        interview.status = "CREATED";
        interview.totalQuestions = 0;
        interview.answeredQuestions = 0;
        interview.score = 0;
        interview.createdAt = LocalDateTime.now();
        interview.updatedAt = LocalDateTime.now();

        interviews.put(
                interviewId,
                interview
        );

        return toResponse(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getUserInterviews(
            User user
    ) {
        List<InterviewResponse> responses =
                new ArrayList<>();

        for (InterviewData interview :
                interviews.values()) {

            if (interview.userId.equals(user.getId())) {
                responses.add(
                        toResponse(interview)
                );
            }
        }

        responses.sort(
                (a, b) -> {
                    if (a.getCreatedAt() == null
                            || b.getCreatedAt() == null) {
                        return 0;
                    }

                    return b.getCreatedAt()
                            .compareTo(a.getCreatedAt());
                }
        );

        return responses;
    }

    @Transactional(readOnly = true)
    public InterviewResponse getInterview(
            Long interviewId,
            User user
    ) {
        InterviewData interview =
                getUserInterview(
                        interviewId,
                        user
                );

        return toResponse(interview);
    }

    public InterviewResponse startInterview(
            Long interviewId,
            User user
    ) {
        InterviewData interview =
                getUserInterview(
                        interviewId,
                        user
                );

        if ("COMPLETED".equals(interview.status)) {
            throw new BadRequestException(
                    "Completed interview cannot be started again"
            );
        }

        interview.status = "IN_PROGRESS";

        if (interview.startedAt == null) {
            interview.startedAt =
                    LocalDateTime.now();
        }

        interview.updatedAt =
                LocalDateTime.now();

        return toResponse(interview);
    }

    public InterviewResultResponse completeInterview(
            Long interviewId,
            User user
    ) {
        InterviewData interview =
                getUserInterview(
                        interviewId,
                        user
                );

        if ("COMPLETED".equals(interview.status)) {
            throw new BadRequestException(
                    "Interview is already completed"
            );
        }

        interview.status = "COMPLETED";
        interview.completedAt =
                LocalDateTime.now();
        interview.updatedAt =
                LocalDateTime.now();

        if (interview.totalQuestions == null) {
            interview.totalQuestions = 0;
        }

        if (interview.answeredQuestions == null) {
            interview.answeredQuestions = 0;
        }

        if (interview.score == null) {
            interview.score = 0;
        }

        return buildResult(interview);
    }

    public void deleteInterview(
            Long interviewId,
            User user
    ) {
        InterviewData interview =
                getUserInterview(
                        interviewId,
                        user
                );

        interviews.remove(interview.id);
    }

    public void updateQuestionCount(
            Long interviewId,
            User user,
            int totalQuestions
    ) {
        if (totalQuestions < 0) {
            throw new BadRequestException(
                    "Question count cannot be negative"
            );
        }

        InterviewData interview =
                getUserInterview(
                        interviewId,
                        user
                );

        interview.totalQuestions =
                totalQuestions;

        interview.updatedAt =
                LocalDateTime.now();
    }

    public void updateAnswerCount(
            Long interviewId,
            User user,
            int answeredQuestions
    ) {
        if (answeredQuestions < 0) {
            throw new BadRequestException(
                    "Answered question count cannot be negative"
            );
        }

        InterviewData interview =
                getUserInterview(
                        interviewId,
                        user
                );

        if (interview.totalQuestions != null
                && answeredQuestions >
                interview.totalQuestions) {

            throw new BadRequestException(
                    "Answered questions cannot exceed total questions"
            );
        }

        interview.answeredQuestions =
                answeredQuestions;

        interview.updatedAt =
                LocalDateTime.now();
    }

    public void updateScore(
            Long interviewId,
            User user,
            int score
    ) {
        if (score < 0) {
            throw new BadRequestException(
                    "Score cannot be negative"
            );
        }

        InterviewData interview =
                getUserInterview(
                        interviewId,
                        user
                );

        interview.score = score;

        interview.updatedAt =
                LocalDateTime.now();
    }

    InterviewData getInterviewData(
            Long interviewId,
            User user
    ) {
        return getUserInterview(
                interviewId,
                user
        );
    }

    private InterviewData getUserInterview(
            Long interviewId,
            User user
    ) {
        InterviewData interview =
                interviews.get(interviewId);

        if (interview == null) {
            throw new ResourceNotFoundException(
                    "Interview not found"
            );
        }

        if (!interview.userId.equals(user.getId())) {
            throw new BadRequestException(
                    "You are not authorized to access this interview"
            );
        }

        return interview;
    }

    private InterviewResponse toResponse(
            InterviewData interview
    ) {
        InterviewResponse response =
                new InterviewResponse();

        response.setId(interview.id);
        response.setUserId(interview.userId);
        response.setTitle(interview.title);
        response.setDescription(interview.description);
        response.setTargetRole(interview.targetRole);
        response.setInterviewType(
                interview.interviewType
        );
        response.setStatus(interview.status);
        response.setTotalQuestions(
                interview.totalQuestions
        );
        response.setAnsweredQuestions(
                interview.answeredQuestions
        );
        response.setScore(interview.score);
        response.setStartedAt(
                interview.startedAt
        );
        response.setCompletedAt(
                interview.completedAt
        );
        response.setCreatedAt(
                interview.createdAt
        );
        response.setUpdatedAt(
                interview.updatedAt
        );

        return response;
    }

    private InterviewResultResponse buildResult(
            InterviewData interview
    ) {
        InterviewResultResponse result =
                new InterviewResultResponse();

        result.setInterviewId(interview.id);
        result.setUserId(interview.userId);
        result.setTitle(interview.title);
        result.setTargetRole(interview.targetRole);
        result.setInterviewType(
                interview.interviewType
        );

        result.setTotalQuestions(
                interview.totalQuestions
        );

        result.setAnsweredQuestions(
                interview.answeredQuestions
        );

        result.setTotalScore(
                interview.score
        );

        int maximumScore =
                interview.totalQuestions == null
                        ? 0
                        : interview.totalQuestions * 10;

        result.setMaximumScore(
                maximumScore
        );

        double percentage = 0.0;

        if (maximumScore > 0
                && interview.score != null) {

            percentage =
                    (interview.score * 100.0)
                            / maximumScore;
        }

        result.setPercentage(
                percentage
        );

        if (percentage >= 80) {
            result.setResult("EXCELLENT");
        } else if (percentage >= 60) {
            result.setResult("GOOD");
        } else if (percentage >= 40) {
            result.setResult("NEEDS_IMPROVEMENT");
        } else {
            result.setResult("BEGINNER");
        }

        result.setFeedback(
                buildFeedback(percentage)
        );

        result.setStrengths(
                new ArrayList<>()
        );

        result.setImprovementAreas(
                new ArrayList<>()
        );

        if (percentage >= 70) {
            result.getStrengths().add(
                    "Good overall interview performance"
            );
        }

        if (percentage < 70) {
            result.getImprovementAreas().add(
                    "Practice more interview questions"
            );
        }

        if (interview.totalQuestions != null
                && interview.answeredQuestions != null
                && interview.answeredQuestions
                < interview.totalQuestions) {

            result.getImprovementAreas().add(
                    "Attempt all interview questions"
            );
        }

        result.setCompletedAt(
                interview.completedAt
        );

        return result;
    }

    private String buildFeedback(
            double percentage
    ) {
        if (percentage >= 80) {
            return "Excellent performance. Continue practicing to maintain consistency.";
        }

        if (percentage >= 60) {
            return "Good performance. Focus on improving weaker areas.";
        }

        if (percentage >= 40) {
            return "Keep practicing interview questions and strengthen your fundamentals.";
        }

        return "More preparation and practice are recommended before the next interview.";
    }

    static class InterviewData {

        private Long id;
        private Long userId;

        private String title;
        private String description;
        private String targetRole;
        private String interviewType;

        private String status;

        private Integer totalQuestions;
        private Integer answeredQuestions;
        private Integer score;

        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private final List<InterviewQuestionResponse>
                questions = new ArrayList<>();

        public List<InterviewQuestionResponse> getQuestions() {
            return questions;
        }
    }
}