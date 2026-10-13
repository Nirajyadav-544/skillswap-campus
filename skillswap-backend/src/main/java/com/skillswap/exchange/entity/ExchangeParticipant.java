package com.skillswap.exchange.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "exchange_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exchange_participant_request_user",
                        columnNames = {
                                "exchange_request_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_exchange_participant_request",
                        columnList = "exchange_request_id"
                ),
                @Index(
                        name = "idx_exchange_participant_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_exchange_participant_active",
                        columnList = "active"
                )
        }
)
public class ExchangeParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "exchange_request_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_exchange_participant_request"
            )
    )
    private ExchangeRequest exchangeRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_exchange_participant_user"
            )
    )
    private User user;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    @Column(
            name = "joined_at",
            nullable = false
    )
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ExchangeParticipant() {
    }

    public ExchangeParticipant(
            ExchangeRequest exchangeRequest,
            User user
    ) {
        this.exchangeRequest = exchangeRequest;
        this.user = user;
        this.active = true;

        LocalDateTime now = LocalDateTime.now();
        this.joinedAt = now;
        this.updatedAt = now;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (joinedAt == null) {
            joinedAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

        if (!active && leftAt == null) {
            leftAt = LocalDateTime.now();
        }

        if (active) {
            leftAt = null;
        }
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

    public void activate() {

        active = true;
        leftAt = null;
    }

    public void deactivate() {

        active = false;
        leftAt = LocalDateTime.now();
    }

    @Override
    public String toString() {

        return "ExchangeParticipant{" +
                "id=" + id +
                ", exchangeRequestId=" +
                (exchangeRequest != null
                        ? exchangeRequest.getId()
                        : null) +
                ", userId=" +
                (user != null
                        ? user.getId()
                        : null) +
                ", active=" + active +
                ", joinedAt=" + joinedAt +
                '}';
    }
}