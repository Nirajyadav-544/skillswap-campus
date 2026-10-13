package com.skillswap.admin.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "admin_audit_logs",
        indexes = {
                @Index(name = "idx_admin_audit_admin", columnList = "admin_id"),
                @Index(name = "idx_admin_audit_action", columnList = "action"),
                @Index(name = "idx_admin_audit_target", columnList = "target_type"),
                @Index(name = "idx_admin_audit_created", columnList = "created_at")
        }
)
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "admin_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_admin_audit_admin")
    )
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AdminAction action;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(length = 2000)
    private String description;

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AdminAuditLog() {
    }

    public AdminAuditLog(
            User admin,
            AdminAction action,
            String targetType,
            Long targetId,
            String description
    ) {
        this.admin = admin;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        normalize();
    }

    @PreUpdate
    protected void onUpdate() {
        normalize();
    }

    private void normalize() {
        if (targetType != null) {
            targetType = targetType.trim().toUpperCase();
        }

        if (description != null) {
            description = description.trim();
        }

        if (ipAddress != null) {
            ipAddress = ipAddress.trim();
        }

        if (userAgent != null) {
            userAgent = userAgent.trim();
        }
    }

    public Long getId() {
        return id;
    }

    public User getAdmin() {
        return admin;
    }

    public AdminAction getAction() {
        return action;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getDescription() {
        return description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public void setAction(AdminAction action) {
        this.action = action;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}