package com.skillswap.exchange.dto;

import com.skillswap.exchange.entity.ExchangeParticipant;

import java.time.LocalDateTime;

public class ExchangeParticipantResponse {

    private Long id;

    private Long exchangeRequestId;

    private Long userId;
    private String userEmail;
    private String userName;

    private boolean active;

    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private LocalDateTime updatedAt;

    public ExchangeParticipantResponse() {
    }

    public static ExchangeParticipantResponse from(
            ExchangeParticipant participant
    ) {
        ExchangeParticipantResponse response =
                new ExchangeParticipantResponse();

        response.id = participant.getId();

        if (participant.getExchangeRequest() != null) {
            response.exchangeRequestId =
                    participant.getExchangeRequest().getId();
        }

        if (participant.getUser() != null) {
            response.userId = participant.getUser().getId();
            response.userEmail = participant.getUser().getEmail();

            response.userName = buildFullName(
                    participant.getUser().getFirstName(),
                    participant.getUser().getLastName()
            );
        }

        response.active = participant.isActive();

        response.joinedAt = participant.getJoinedAt();
        response.leftAt = participant.getLeftAt();
        response.updatedAt = participant.getUpdatedAt();

        return response;
    }

    private static String buildFullName(
            String firstName,
            String lastName
    ) {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();

        if (first.isEmpty() && last.isEmpty()) {
            return null;
        }

        if (first.isEmpty()) {
            return last;
        }

        if (last.isEmpty()) {
            return first;
        }

        return first + " " + last;
    }

    public Long getId() {
        return id;
    }

    public Long getExchangeRequestId() {
        return exchangeRequestId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}