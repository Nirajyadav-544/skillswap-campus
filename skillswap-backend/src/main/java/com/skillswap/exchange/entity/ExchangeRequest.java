package com.skillswap.exchange.entity;

import com.skillswap.identity.entity.User;
import com.skillswap.matching.entity.MatchResult;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "exchange_requests",
        indexes = {
                @Index(
                        name = "idx_exchange_request_requester",
                        columnList = "requester_user_id"
                ),
                @Index(
                        name = "idx_exchange_request_recipient",
                        columnList = "recipient_user_id"
                ),
                @Index(
                        name = "idx_exchange_request_match",
                        columnList = "match_result_id"
                ),
                @Index(
                        name = "idx_exchange_request_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_exchange_request_type",
                        columnList = "exchange_type"
                ),
                @Index(
                        name = "idx_exchange_request_created",
                        columnList = "created_at"
                )
        }
)
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requester_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_exchange_request_requester"
            )
    )
    private User requesterUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "recipient_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_exchange_request_recipient"
            )
    )
    private User recipientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "match_result_id",
            foreignKey = @ForeignKey(
                    name = "fk_exchange_request_match"
            )
    )
    private MatchResult matchResult;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "exchange_type",
            nullable = false,
            length = 30
    )
    private ExchangeType exchangeType = ExchangeType.ONE_WAY;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private ExchangeStatus status = ExchangeStatus.PENDING;

    @Column(
            name = "request_message",
            length = 2000
    )
    private String requestMessage;

    @Column(
            name = "response_message",
            length = 2000
    )
    private String responseMessage;

    @Column(
            name = "requested_skill_name",
            length = 200
    )
    private String requestedSkillName;

    @Column(
            name = "offered_skill_name",
            length = 200
    )
    private String offeredSkillName;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public ExchangeRequest() {
    }

    public ExchangeRequest(
            User requesterUser,
            User recipientUser,
            ExchangeType exchangeType
    ) {
        this.requesterUser = requesterUser;
        this.recipientUser = recipientUser;

        this.exchangeType = exchangeType != null
                ? exchangeType
                : ExchangeType.ONE_WAY;

        this.status = ExchangeStatus.PENDING;

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (exchangeType == null) {
            exchangeType = ExchangeType.ONE_WAY;
        }

        if (status == null) {
            status = ExchangeStatus.PENDING;
        }

        normalizeText();
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

        if (exchangeType == null) {
            exchangeType = ExchangeType.ONE_WAY;
        }

        if (status == null) {
            status = ExchangeStatus.PENDING;
        }

        normalizeText();
    }

    private void normalizeText() {

        if (requestMessage != null) {
            requestMessage = requestMessage.trim();

            if (requestMessage.isBlank()) {
                requestMessage = null;
            }
        }

        if (responseMessage != null) {
            responseMessage = responseMessage.trim();

            if (responseMessage.isBlank()) {
                responseMessage = null;
            }
        }

        if (requestedSkillName != null) {
            requestedSkillName = requestedSkillName.trim();

            if (requestedSkillName.isBlank()) {
                requestedSkillName = null;
            }
        }

        if (offeredSkillName != null) {
            offeredSkillName = offeredSkillName.trim();

            if (offeredSkillName.isBlank()) {
                offeredSkillName = null;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public User getRequesterUser() {
        return requesterUser;
    }

    public void setRequesterUser(User requesterUser) {
        this.requesterUser = requesterUser;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public ExchangeStatus getStatus() {
        return status;
    }

    public void setStatus(ExchangeStatus status) {
        this.status = status;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getRequestedSkillName() {
        return requestedSkillName;
    }

    public void setRequestedSkillName(String requestedSkillName) {
        this.requestedSkillName = requestedSkillName;
    }

    public String getOfferedSkillName() {
        return offeredSkillName;
    }

    public void setOfferedSkillName(String offeredSkillName) {
        this.offeredSkillName = offeredSkillName;
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

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPending() {
        return status == ExchangeStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == ExchangeStatus.ACCEPTED;
    }

    public boolean isRejected() {
        return status == ExchangeStatus.REJECTED;
    }

    public boolean isCancelled() {
        return status == ExchangeStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return status == ExchangeStatus.COMPLETED;
    }

    public boolean isExpired() {

        if (status == ExchangeStatus.EXPIRED) {
            return true;
        }

        return expiresAt != null
                && expiresAt.isBefore(LocalDateTime.now())
                && status == ExchangeStatus.PENDING;
    }

    public boolean isClosed() {

        return status == ExchangeStatus.REJECTED
                || status == ExchangeStatus.CANCELLED
                || status == ExchangeStatus.EXPIRED
                || status == ExchangeStatus.COMPLETED
                || status == ExchangeStatus.ARCHIVED;
    }

    public boolean isValidParticipantPair() {

        if (requesterUser == null
                || recipientUser == null) {
            return false;
        }

        if (requesterUser.getId() == null
                || recipientUser.getId() == null) {
            return true;
        }

        return !requesterUser.getId()
                .equals(recipientUser.getId());
    }

    public void markAccepted() {

        status = ExchangeStatus.ACCEPTED;

        LocalDateTime now = LocalDateTime.now();

        respondedAt = now;
        acceptedAt = now;
        rejectedAt = null;
        cancelledAt = null;
    }

    public void markRejected(String message) {

        status = ExchangeStatus.REJECTED;

        LocalDateTime now = LocalDateTime.now();

        respondedAt = now;
        rejectedAt = now;
        acceptedAt = null;

        setResponseMessage(message);
    }

    public void markCancelled() {

        status = ExchangeStatus.CANCELLED;

        cancelledAt = LocalDateTime.now();
    }

    public void markCompleted() {

        status = ExchangeStatus.COMPLETED;

        completedAt = LocalDateTime.now();
    }

    public void markExpired() {

        status = ExchangeStatus.EXPIRED;
    }

    public void archive() {

        status = ExchangeStatus.ARCHIVED;
    }

    @Override
    public String toString() {

        return "ExchangeRequest{" +
                "id=" + id +
                ", requesterUserId=" +
                (requesterUser != null
                        ? requesterUser.getId()
                        : null) +
                ", recipientUserId=" +
                (recipientUser != null
                        ? recipientUser.getId()
                        : null) +
                ", exchangeType=" + exchangeType +
                ", status=" + status +
                ", requestedSkillName='" +
                requestedSkillName + '\'' +
                ", offeredSkillName='" +
                offeredSkillName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}