package com.skillswap.identity.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "otp_verifications",
        indexes = {
                @Index(
                        name = "idx_otp_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_otp_target",
                        columnList = "target"
                ),
                @Index(
                        name = "idx_otp_expires",
                        columnList = "expires_at"
                )
        }
)
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private VerificationMethod method;

    @Column(
            nullable = false,
            length = 255
    )
    private String target;

    @Column(
            name = "otp_hash",
            nullable = false,
            length = 255
    )
    private String otpHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "last_sent_at",
            nullable = false
    )
    private LocalDateTime lastSentAt;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(
            name = "max_attempts",
            nullable = false
    )
    private int maxAttempts = 5;

    @Column(nullable = false)
    private boolean used = false;

    @Column(
            name = "resend_count",
            nullable = false
    )
    private int resendCount = 0;

    public boolean isExpired() {
        return expiresAt == null ||
                expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean hasExceededAttempts() {
        return attempts >= maxAttempts;
    }

    public boolean canBeUsed() {
        return !used &&
                !isExpired() &&
                !hasExceededAttempts();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VerificationMethod getMethod() {
        return method;
    }

    public void setMethod(VerificationMethod method) {
        this.method = method;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(LocalDateTime lastSentAt) {
        this.lastSentAt = lastSentAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getResendCount() {
        return resendCount;
    }

    public void setResendCount(int resendCount) {
        this.resendCount = resendCount;
    }
}