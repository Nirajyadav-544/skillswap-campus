package com.skillswap.ai.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ai_interactions",
        indexes = {
                @Index(
                        name = "idx_ai_interaction_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_ai_interaction_type",
                        columnList = "interaction_type"
                ),
                @Index(
                        name = "idx_ai_interaction_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_ai_interaction_created",
                        columnList = "created_at"
                )
        }
)
public class AiInteraction {

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
            name = "interaction_type",
            nullable = false,
            length = 50
    )
    private AiInteractionType interactionType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private AiInteractionStatus status =
            AiInteractionStatus.PENDING;

    @Column(
            name = "prompt",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String prompt;

    @Column(
            name = "response",
            columnDefinition = "TEXT"
    )
    private String response;

    @Column(
            name = "error_message",
            columnDefinition = "TEXT"
    )
    private String errorMessage;

    @Column(
            name = "context_type",
            length = 100
    )
    private String contextType;

    @Column(
            name = "context_id"
    )
    private Long contextId;

    @Column(
            name = "provider",
            length = 100
    )
    private String provider;

    @Column(
            name = "model_name",
            length = 150
    )
    private String modelName;

    @Column(
            name = "tokens_used"
    )
    private Integer tokensUsed;

    @Column(
            name = "processing_time_ms"
    )
    private Long processingTimeMs;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AiInteraction() {
    }

    public AiInteraction(
            User user,
            AiInteractionType interactionType,
            String prompt
    ) {
        this.user = user;
        this.interactionType = interactionType;
        this.prompt = prompt;
        this.status = AiInteractionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now =
                LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (status == null) {
            status =
                    AiInteractionStatus.PENDING;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt =
                LocalDateTime.now();
    }

    public void markProcessing() {
        status =
                AiInteractionStatus.PROCESSING;

        startedAt =
                LocalDateTime.now();

        updatedAt =
                LocalDateTime.now();
    }

    public void markCompleted(
            String response
    ) {
        this.response = response;

        this.status =
                AiInteractionStatus.COMPLETED;

        this.completedAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();

        calculateProcessingTime();
    }

    public void markFailed(
            String errorMessage
    ) {
        this.errorMessage =
                errorMessage;

        this.status =
                AiInteractionStatus.FAILED;

        this.completedAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();

        calculateProcessingTime();
    }

    public void cancel() {
        this.status =
                AiInteractionStatus.CANCELLED;

        this.completedAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();

        calculateProcessingTime();
    }

    private void calculateProcessingTime() {
        if (startedAt != null
                && completedAt != null) {

            processingTimeMs =
                    java.time.Duration
                            .between(
                                    startedAt,
                                    completedAt
                            )
                            .toMillis();
        }
    }

    public boolean isPending() {
        return status ==
                AiInteractionStatus.PENDING;
    }

    public boolean isProcessing() {
        return status ==
                AiInteractionStatus.PROCESSING;
    }

    public boolean isCompleted() {
        return status ==
                AiInteractionStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status ==
                AiInteractionStatus.FAILED;
    }

    public boolean isCancelled() {
        return status ==
                AiInteractionStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AiInteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(
            AiInteractionType interactionType
    ) {
        this.interactionType =
                interactionType;
    }

    public AiInteractionStatus getStatus() {
        return status;
    }

    public void setStatus(
            AiInteractionStatus status
    ) {
        this.status = status;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(
            String errorMessage
    ) {
        this.errorMessage =
                errorMessage;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(
            String contextType
    ) {
        this.contextType =
                contextType;
    }

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(
            Integer tokensUsed
    ) {
        this.tokensUsed = tokensUsed;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(
            Long processingTimeMs
    ) {
        this.processingTimeMs =
                processingTimeMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(
            LocalDateTime startedAt
    ) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(
            LocalDateTime completedAt
    ) {
        this.completedAt =
                completedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }
}