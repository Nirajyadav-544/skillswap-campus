package com.skillswap.career.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "career_roadmap_items",
        indexes = {
                @Index(
                        name = "idx_roadmap_item_roadmap_id",
                        columnList = "roadmap_id"
                ),
                @Index(
                        name = "idx_roadmap_item_status",
                        columnList = "status"
                )
        }
)
public class CareerRoadmapItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "roadmap_id",
            nullable = false
    )
    private CareerRoadmap roadmap;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            name = "description",
            length = 2000
    )
    private String description;

    @Column(
            name = "skill_name",
            length = 200
    )
    private String skillName;

    @Column(
            name = "resource_url",
            length = 1000
    )
    private String resourceUrl;

    @Column(
            name = "sequence_number",
            nullable = false
    )
    private Integer sequenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private CareerRoadmapItemStatus status;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(
            name = "completed_at"
    )
    private LocalDateTime completedAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public CareerRoadmapItem() {
    }

    public CareerRoadmapItem(
            String title,
            String description,
            String skillName,
            String resourceUrl,
            Integer sequenceNumber,
            LocalDate targetDate
    ) {
        this.title = title;
        this.description = description;
        this.skillName = skillName;
        this.resourceUrl = resourceUrl;
        this.sequenceNumber = sequenceNumber;
        this.targetDate = targetDate;
        this.status = CareerRoadmapItemStatus.NOT_STARTED;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = CareerRoadmapItemStatus.NOT_STARTED;
        }

        if (sequenceNumber == null) {
            sequenceNumber = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void markCompleted() {
        this.status = CareerRoadmapItemStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markInProgress() {
        this.status = CareerRoadmapItemStatus.IN_PROGRESS;
        this.completedAt = null;
    }

    public void markNotStarted() {
        this.status = CareerRoadmapItemStatus.NOT_STARTED;
        this.completedAt = null;
    }

    public void markSkipped() {
        this.status = CareerRoadmapItemStatus.SKIPPED;
        this.completedAt = null;
    }

    public Long getId() {
        return id;
    }

    public CareerRoadmap getRoadmap() {
        return roadmap;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public CareerRoadmapItemStatus getStatus() {
        return status;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoadmap(CareerRoadmap roadmap) {
        this.roadmap = roadmap;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setStatus(CareerRoadmapItemStatus status) {
        this.status = status;

        if (status != CareerRoadmapItemStatus.COMPLETED) {
            this.completedAt = null;
        }
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}