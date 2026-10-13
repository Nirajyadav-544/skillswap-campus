package com.skillswap.interview.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.interview.dto.InterviewQuestionResponse;
import com.skillswap.interview.dto.SubmitInterviewAnswerRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class InterviewQuestionService {

    private final InterviewService interviewService;

    private final AtomicLong questionIdGenerator =
            new AtomicLong(1);

    public InterviewQuestionService(
            InterviewService interviewService
    ) {
        this.interviewService = interviewService;
    }

    public InterviewQuestionResponse addQuestion(
            Long interviewId,
            User user,
            String question,
            String questionType,
            String difficulty,
            String expectedAnswer
    ) {
        if (question == null || question.isBlank()) {
            throw new BadRequestException(
                    "Question is required"
            );
        }

        InterviewService.InterviewData interview =
                interviewService.getInterviewData(
                        interviewId,
                        user
                );

        InterviewQuestionResponse response =
                new InterviewQuestionResponse();

        response.setId(
                questionIdGenerator.getAndIncrement()
        );

        response.setInterviewId(interviewId);

        response.setQuestionNumber(
                interview.getQuestions().size() + 1
        );

        response.setQuestion(question);
        response.setQuestionType(questionType);
        response.setDifficulty(difficulty);
        response.setExpectedAnswer(expectedAnswer);
        response.setUserAnswer(null);
        response.setAnswered(false);
        response.setScore(0);

        interview.getQuestions().add(response);

        interviewService.updateQuestionCount(
                interviewId,
                user,
                interview.getQuestions().size()
        );

        return response;
    }

    @Transactional(readOnly = true)
    public List<InterviewQuestionResponse> getQuestions(
            Long interviewId,
            User user
    ) {
        InterviewService.InterviewData interview =
                interviewService.getInterviewData(
                        interviewId,
                        user
                );

        return new ArrayList<>(
                interview.getQuestions()
        );
    }

    @Transactional(readOnly = true)
    public InterviewQuestionResponse getQuestion(
            Long interviewId,
            Long questionId,
            User user
    ) {
        InterviewService.InterviewData interview =
                interviewService.getInterviewData(
                        interviewId,
                        user
                );

        return findQuestion(
                interview,
                questionId
        );
    }

    public InterviewQuestionResponse submitAnswer(
            Long interviewId,
            Long questionId,
            User user,
            SubmitInterviewAnswerRequest request
    ) {
        if (request == null
                || request.getAnswer() == null
                || request.getAnswer().isBlank()) {

            throw new BadRequestException(
                    "Answer is required"
            );
        }

        InterviewService.InterviewData interview =
                interviewService.getInterviewData(
                        interviewId,
                        user
                );

        InterviewQuestionResponse question =
                findQuestion(
                        interview,
                        questionId
                );

        question.setUserAnswer(
                request.getAnswer()
        );

        question.setAnswered(true);

        if (question.getScore() == null) {
            question.setScore(0);
        }

        int answeredCount = 0;

        for (InterviewQuestionResponse item :
                interview.getQuestions()) {

            if (Boolean.TRUE.equals(
                    item.getAnswered())) {
                answeredCount++;
            }
        }

        interviewService.updateAnswerCount(
                interviewId,
                user,
                answeredCount
        );

        return question;
    }

    public void deleteQuestion(
            Long interviewId,
            Long questionId,
            User user
    ) {
        InterviewService.InterviewData interview =
                interviewService.getInterviewData(
                        interviewId,
                        user
                );

        InterviewQuestionResponse question =
                findQuestion(
                        interview,
                        questionId
                );

        interview.getQuestions().remove(question);

        renumberQuestions(interview);

        interviewService.updateQuestionCount(
                interviewId,
                user,
                interview.getQuestions().size()
        );

        int answeredCount = 0;

        for (InterviewQuestionResponse item :
                interview.getQuestions()) {

            if (Boolean.TRUE.equals(
                    item.getAnswered())) {
                answeredCount++;
            }
        }

        interviewService.updateAnswerCount(
                interviewId,
                user,
                answeredCount
        );
    }

    public InterviewQuestionResponse scoreQuestion(
            Long interviewId,
            Long questionId,
            User user,
            int score
    ) {
        if (score < 0 || score > 10) {
            throw new BadRequestException(
                    "Question score must be between 0 and 10"
            );
        }

        InterviewService.InterviewData interview =
                interviewService.getInterviewData(
                        interviewId,
                        user
                );

        InterviewQuestionResponse question =
                findQuestion(
                        interview,
                        questionId
                );

        question.setScore(score);

        int totalScore = 0;

        for (InterviewQuestionResponse item :
                interview.getQuestions()) {

            if (item.getScore() != null) {
                totalScore += item.getScore();
            }
        }

        interviewService.updateScore(
                interviewId,
                user,
                totalScore
        );

        return question;
    }

    private InterviewQuestionResponse findQuestion(
            InterviewService.InterviewData interview,
            Long questionId
    ) {
        for (InterviewQuestionResponse question :
                interview.getQuestions()) {

            if (question.getId().equals(questionId)) {
                return question;
            }
        }

        throw new ResourceNotFoundException(
                "Interview question not found"
        );
    }

    private void renumberQuestions(
            InterviewService.InterviewData interview
    ) {
        int number = 1;

        for (InterviewQuestionResponse question :
                interview.getQuestions()) {

            question.setQuestionNumber(number++);
        }
    }
}