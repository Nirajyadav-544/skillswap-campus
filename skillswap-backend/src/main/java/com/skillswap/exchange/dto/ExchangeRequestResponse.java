package com.skillswap.exchange.dto;

import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.exchange.entity.ExchangeType;

import java.time.LocalDateTime;

public class ExchangeRequestResponse {

    private Long id;

    private Long requesterUserId;
    private String requesterEmail;
    private String requesterName;

    private Long recipientUserId;
    private String recipientEmail;
    private String recipientName;

    private Long matchResultId;

    private ExchangeType exchangeType;
    private ExchangeStatus status;

    private String requestMessage;
    private String responseMessage;

    private String requestedSkillName;
    private String offeredSkillName;

    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime updatedAt;

    private boolean pending;
    private boolean accepted;
    private boolean rejected;
    private boolean cancelled;
    private boolean completed;
    private boolean expired;
    private boolean closed;

    public ExchangeRequestResponse() {
    }

    public static ExchangeRequestResponse from(ExchangeRequest request) {
        ExchangeRequestResponse response = new ExchangeRequestResponse();

        response.id = request.getId();

        if (request.getRequesterUser() != null) {
            response.requesterUserId = request.getRequesterUser().getId();
            response.requesterEmail = request.getRequesterUser().getEmail();

            String firstName = request.getRequesterUser().getFirstName();
            String lastName = request.getRequesterUser().getLastName();

            response.requesterName = buildFullName(firstName, lastName);
        }

        if (request.getRecipientUser() != null) {
            response.recipientUserId = request.getRecipientUser().getId();
            response.recipientEmail = request.getRecipientUser().getEmail();

            String firstName = request.getRecipientUser().getFirstName();
            String lastName = request.getRecipientUser().getLastName();

            response.recipientName = buildFullName(firstName, lastName);
        }

        if (request.getMatchResult() != null) {
            response.matchResultId = request.getMatchResult().getId();
        }

        response.exchangeType = request.getExchangeType();
        response.status = request.getStatus();

        response.requestMessage = request.getRequestMessage();
        response.responseMessage = request.getResponseMessage();

        response.requestedSkillName = request.getRequestedSkillName();
        response.offeredSkillName = request.getOfferedSkillName();

        response.createdAt = request.getCreatedAt();
        response.respondedAt = request.getRespondedAt();
        response.acceptedAt = request.getAcceptedAt();
        response.rejectedAt = request.getRejectedAt();
        response.cancelledAt = request.getCancelledAt();
        response.completedAt = request.getCompletedAt();
        response.expiresAt = request.getExpiresAt();
        response.updatedAt = request.getUpdatedAt();

        response.pending = request.isPending();
        response.accepted = request.isAccepted();
        response.rejected = request.isRejected();
        response.cancelled = request.isCancelled();
        response.completed = request.isCompleted();
        response.expired = request.isExpired();
        response.closed = request.isClosed();

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

    public Long getRequesterUserId() {
        return requesterUserId;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public Long getMatchResultId() {
        return matchResultId;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public ExchangeStatus getStatus() {
        return status;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getRequestedSkillName() {
        return requestedSkillName;
    }

    public String getOfferedSkillName() {
        return offeredSkillName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean isClosed() {
        return closed;
    }
}