package com.skillswap.exchange.dto;

import com.skillswap.exchange.entity.ExchangeRequest;
import com.skillswap.exchange.entity.ExchangeStatus;
import com.skillswap.exchange.entity.ExchangeType;

import java.time.LocalDateTime;

public class ExchangeSummaryResponse {

    private Long id;

    private Long requesterUserId;
    private String requesterName;

    private Long recipientUserId;
    private String recipientName;

    private ExchangeType exchangeType;
    private ExchangeStatus status;

    private String requestedSkillName;
    private String offeredSkillName;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private boolean pending;
    private boolean accepted;
    private boolean completed;
    private boolean closed;

    public ExchangeSummaryResponse() {
    }

    public static ExchangeSummaryResponse from(ExchangeRequest request) {
        ExchangeSummaryResponse response = new ExchangeSummaryResponse();

        response.id = request.getId();

        if (request.getRequesterUser() != null) {
            response.requesterUserId =
                    request.getRequesterUser().getId();

            response.requesterName = buildFullName(
                    request.getRequesterUser().getFirstName(),
                    request.getRequesterUser().getLastName()
            );
        }

        if (request.getRecipientUser() != null) {
            response.recipientUserId =
                    request.getRecipientUser().getId();

            response.recipientName = buildFullName(
                    request.getRecipientUser().getFirstName(),
                    request.getRecipientUser().getLastName()
            );
        }

        response.exchangeType = request.getExchangeType();
        response.status = request.getStatus();

        response.requestedSkillName =
                request.getRequestedSkillName();

        response.offeredSkillName =
                request.getOfferedSkillName();

        response.createdAt = request.getCreatedAt();
        response.expiresAt = request.getExpiresAt();

        response.pending = request.isPending();
        response.accepted = request.isAccepted();
        response.completed = request.isCompleted();
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

    public String getRequesterName() {
        return requesterName;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public ExchangeStatus getStatus() {
        return status;
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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isClosed() {
        return closed;
    }
}