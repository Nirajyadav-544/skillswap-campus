package com.skillswap.career.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "career_roadmaps",
        indexes = {
                @Index(
                        name = "idx_career_roadmap_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_career_roadmap_goal_id",
                        columnList = "career_goal_id"
                )
        }
)
public class CareerRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_goal_id")
    private CareerGoal careerGoal;

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
            name = "target_role",
            length = 200
    )
    private String targetRole;

    @Column(
            name = "total_items",
            nullable = false
    )
    private Integer totalItems = 0;

    @Column(
            name = "completed_items",
            nullable = false
    )
    private Integer completedItems = 0;

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

    @OneToMany(
            mappedBy = "roadmap",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sequenceNumber ASC")
    private List<CareerRoadmapItem> items = new ArrayList<>();

    public CareerRoadmap() {
    }

    public CareerRoadmap(
            User user,
            CareerGoal careerGoal,
            String title,
            String description,
            String targetRole
    ) {
        this.user = user;
        this.careerGoal = careerGoal;
        this.title = title;
        this.description = description;
        this.targetRole = targetRole;
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

        if (totalItems == null) {
            totalItems = 0;
        }

        if (completedItems == null) {
            completedItems = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addItem(CareerRoadmapItem item) {
        if (item == null) {
            return;
        }

        items.add(item);
        item.setRoadmap(this);
        recalculateProgress();
    }

    public void removeItem(CareerRoadmapItem item) {
        if (item == null) {
            return;
        }

        items.remove(item);
        item.setRoadmap(null);
        recalculateProgress();
    }

    public void recalculateProgress() {
        totalItems = items.size();

        completedItems = (int) items.stream()
                .filter(item ->
                        item.getStatus()
                                == CareerRoadmapItemStatus.COMPLETED
                )
                .count();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CareerGoal getCareerGoal() {
        return careerGoal;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public Integer getCompletedItems() {
        return completedItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<CareerRoadmapItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCareerGoal(CareerGoal careerGoal) {
        this.careerGoal = careerGoal;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public void setCompletedItems(Integer completedItems) {
        this.completedItems = completedItems;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setItems(List<CareerRoadmapItem> items) {
        this.items = items != null
                ? items
                : new ArrayList<>();

        recalculateProgress();
    }
}