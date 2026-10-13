package com.skillswap.college.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "college_members",
        indexes = {
                @Index(name = "idx_college_member_college", columnList = "college_id"),
                @Index(name = "idx_college_member_user", columnList = "user_id"),
                @Index(name = "idx_college_member_role", columnList = "role")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_college_member_college_user",
                        columnNames = {"college_id", "user_id"}
                )
        }
)
public class CollegeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "college_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_college_member_college")
    )
    private College college;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_college_member_user")
    )
    private User user;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public CollegeMember() {
    }

    public CollegeMember(College college, User user, String role) {
        this.college = college;
        this.user = user;
        this.role = role;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (joinedAt == null) {
            joinedAt = now;
        }

        updatedAt = now;

        if (active == null) {
            active = true;
        }

        normalize();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalize();
    }

    private void normalize() {
        if (role != null) {
            role = role.trim().toUpperCase();
        }
    }

    public Long getId() {
        return id;
    }

    public College getCollege() {
        return college;
    }

    public User getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}