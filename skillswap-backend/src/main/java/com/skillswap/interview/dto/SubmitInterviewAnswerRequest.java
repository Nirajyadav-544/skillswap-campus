package com.skillswap.interview.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmitInterviewAnswerRequest {

    @NotBlank(message = "Answer is required")
    private String answer;

    public SubmitInterviewAnswerRequest() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}