package com.skillswap.exchange.dto;

import com.skillswap.exchange.entity.ExchangeOffer;
import com.skillswap.exchange.entity.ExchangeResponseStatus;

import java.time.LocalDateTime;

public class ExchangeOfferResponse {

    private Long id;

    private Long exchangeRequestId;

    private Long userId;
    private String userEmail;
    private String userName;

    private String skillName;
    private Long skillId;
    private String offerDescription;

    private ExchangeResponseStatus responseStatus;

    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private LocalDateTime updatedAt;

    private boolean pending;
    private boolean accepted;
    private boolean rejected;
    private boolean cancelled;

    public ExchangeOfferResponse() {
    }

    public static ExchangeOfferResponse from(ExchangeOffer offer) {
        ExchangeOfferResponse response =
                new ExchangeOfferResponse();

        response.id = offer.getId();

        if (offer.getExchangeRequest() != null) {
            response.exchangeRequestId =
                    offer.getExchangeRequest().getId();
        }

        if (offer.getUser() != null) {
            response.userId = offer.getUser().getId();
            response.userEmail = offer.getUser().getEmail();

            response.userName = buildFullName(
                    offer.getUser().getFirstName(),
                    offer.getUser().getLastName()
            );
        }

        response.skillName = offer.getSkillName();
        response.skillId = offer.getSkillId();
        response.offerDescription =
                offer.getOfferDescription();

        response.responseStatus =
                offer.getResponseStatus();

        response.createdAt = offer.getCreatedAt();
        response.respondedAt = offer.getRespondedAt();
        response.updatedAt = offer.getUpdatedAt();

        response.pending = offer.isPending();
        response.accepted = offer.isAccepted();
        response.rejected = offer.isRejected();
        response.cancelled = offer.isCancelled();

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

    public String getSkillName() {
        return skillName;
    }

    public Long getSkillId() {
        return skillId;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public ExchangeResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
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
}