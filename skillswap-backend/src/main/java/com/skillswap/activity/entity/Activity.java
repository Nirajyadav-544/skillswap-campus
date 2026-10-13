package com.skillswap.activity.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "activities",
        indexes = {
                @Index(
                        name = "idx_activity_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_activity_created",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_activity_type",
                        columnList = "type"
                )
        }
)
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_activity_user"
            )
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private ActivityType type;

    @Column(
            nullable = false,
            length = 500
    )
    private String message;

    @Column(
            name = "reference_id"
    )
    private Long referenceId;

    @Column(
            name = "reference_type",
            length = 100
    )
    private String referenceType;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ActivityType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}