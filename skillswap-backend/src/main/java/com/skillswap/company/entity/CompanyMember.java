package com.skillswap.company.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "company_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_company_member",
                        columnNames = {"company_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_company_member_company", columnList = "company_id"),
                @Index(name = "idx_company_member_user", columnList = "user_id")
        }
)
public class CompanyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "company_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_company_member_company")
    )
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_company_member_user")
    )
    private User user;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    public CompanyMember() {
    }

    public CompanyMember(
            Company company,
            User user,
            String role
    ) {
        this.company = company;
        this.user = user;
        this.role = role;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }

        if (active == null) {
            active = true;
        }

        normalize();
    }

    @PreUpdate
    protected void onUpdate() {
        normalize();
    }

    private void normalize() {
        if (role != null) {
            role = role.trim();
        }
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setCompany(Company company) {
        this.company = company;
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
}