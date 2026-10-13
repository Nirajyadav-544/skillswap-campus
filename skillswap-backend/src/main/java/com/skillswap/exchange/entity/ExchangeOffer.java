package com.skillswap.exchange.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "exchange_offers",
        indexes = {
                @Index(
                        name = "idx_exchange_offer_request",
                        columnList = "exchange_request_id"
                ),
                @Index(
                        name = "idx_exchange_offer_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_exchange_offer_status",
                        columnList = "response_status"
                ),
                @Index(
                        name = "idx_exchange_offer_created",
                        columnList = "created_at"
                )
        }
)
public class ExchangeOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "exchange_request_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_exchange_offer_request"
            )
    )
    private ExchangeRequest exchangeRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_exchange_offer_user"
            )
    )
    private User user;

    @Column(
            name = "skill_name",
            nullable = false,
            length = 200
    )
    private String skillName;

    @Column(
            name = "skill_id"
    )
    private Long skillId;

    @Column(
            name = "offer_description",
            length = 2000
    )
    private String offerDescription;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "response_status",
            nullable = false,
            length = 30
    )
    private ExchangeResponseStatus responseStatus =
            ExchangeResponseStatus.PENDING;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ExchangeOffer() {
    }

    public ExchangeOffer(
            ExchangeRequest exchangeRequest,
            User user,
            String skillName
    ) {
        this.exchangeRequest = exchangeRequest;
        this.user = user;
        this.skillName = normalize(skillName);
        this.responseStatus = ExchangeResponseStatus.PENDING;

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

        if (responseStatus == null) {
            responseStatus = ExchangeResponseStatus.PENDING;
        }

        skillName = normalize(skillName);
        offerDescription = normalize(offerDescription);
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

        if (responseStatus == null) {
            responseStatus = ExchangeResponseStatus.PENDING;
        }

        skillName = normalize(skillName);
        offerDescription = normalize(offerDescription);
    }

    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank() ? null : normalized;
    }

    public Long getId() {
        return id;
    }

    public ExchangeRequest getExchangeRequest() {
        return exchangeRequest;
    }

    public void setExchangeRequest(
            ExchangeRequest exchangeRequest
    ) {
        this.exchangeRequest = exchangeRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public ExchangeResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(
            ExchangeResponseStatus responseStatus
    ) {
        this.responseStatus = responseStatus;
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
        return responseStatus ==
                ExchangeResponseStatus.PENDING;
    }

    public boolean isAccepted() {
        return responseStatus ==
                ExchangeResponseStatus.ACCEPTED;
    }

    public boolean isRejected() {
        return responseStatus ==
                ExchangeResponseStatus.REJECTED;
    }

    public boolean isCancelled() {
        return responseStatus ==
                ExchangeResponseStatus.CANCELLED;
    }

    public void markAccepted() {

        responseStatus = ExchangeResponseStatus.ACCEPTED;
        respondedAt = LocalDateTime.now();
    }

    public void markRejected() {

        responseStatus = ExchangeResponseStatus.REJECTED;
        respondedAt = LocalDateTime.now();
    }

    public void markCancelled() {

        responseStatus = ExchangeResponseStatus.CANCELLED;
        respondedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {

        return "ExchangeOffer{" +
                "id=" + id +
                ", exchangeRequestId=" +
                (exchangeRequest != null
                        ? exchangeRequest.getId()
                        : null) +
                ", userId=" +
                (user != null
                        ? user.getId()
                        : null) +
                ", skillName='" + skillName + '\'' +
                ", skillId=" + skillId +
                ", responseStatus=" + responseStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}