package com.skillswap.company.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "companies",
        indexes = {
                @Index(name = "idx_company_name", columnList = "name"),
                @Index(name = "idx_company_status", columnList = "status"),
                @Index(name = "idx_company_owner", columnList = "owner_id")
        }
)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String legalName;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String website;

    @Column(length = 150)
    private String industry;

    @Column(length = 150)
    private String companySize;

    @Column(length = 150)
    private String headquarters;

    @Column(length = 255)
    private String logoUrl;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CompanyStatus status = CompanyStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_company_owner")
    )
    private User owner;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Company() {
    }

    public Company(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.status = CompanyStatus.PENDING;
        this.verified = false;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (status == null) {
            status = CompanyStatus.PENDING;
        }

        if (verified == null) {
            verified = false;
        }

        normalize();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalize();
    }

    private void normalize() {
        if (name != null) {
            name = name.trim();
        }

        if (legalName != null) {
            legalName = legalName.trim();
        }

        if (description != null) {
            description = description.trim();
        }

        if (website != null) {
            website = website.trim();
        }

        if (industry != null) {
            industry = industry.trim();
        }

        if (companySize != null) {
            companySize = companySize.trim();
        }

        if (headquarters != null) {
            headquarters = headquarters.trim();
        }

        if (logoUrl != null) {
            logoUrl = logoUrl.trim();
        }

        if (email != null) {
            email = email.trim().toLowerCase();
        }

        if (phoneNumber != null) {
            phoneNumber = phoneNumber.trim();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public String getIndustry() {
        return industry;
    }

    public String getCompanySize() {
        return companySize;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public User getOwner() {
        return owner;
    }

    public Boolean getVerified() {
        return verified;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return status == CompanyStatus.ACTIVE;
    }

    public boolean isPending() {
        return status == CompanyStatus.PENDING;
    }

    public boolean isSuspended() {
        return status == CompanyStatus.SUSPENDED;
    }

    public boolean isArchived() {
        return status == CompanyStatus.ARCHIVED;
    }
}