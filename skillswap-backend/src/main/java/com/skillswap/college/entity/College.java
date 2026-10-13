package com.skillswap.college.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "colleges",
        indexes = {
                @Index(name = "idx_college_name", columnList = "name"),
                @Index(name = "idx_college_status", columnList = "status"),
                @Index(name = "idx_college_owner", columnList = "owner_id")
        }
)
public class College {

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
    private String location;

    @Column(length = 150)
    private String city;

    @Column(length = 150)
    private String state;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    private String postalCode;

    @Column(length = 255)
    private String logoUrl;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CollegeStatus status = CollegeStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_college_owner")
    )
    private User owner;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public College() {
    }

    public College(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.status = CollegeStatus.PENDING;
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
            status = CollegeStatus.PENDING;
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

        if (location != null) {
            location = location.trim();
        }

        if (city != null) {
            city = city.trim();
        }

        if (state != null) {
            state = state.trim();
        }

        if (country != null) {
            country = country.trim();
        }

        if (postalCode != null) {
            postalCode = postalCode.trim();
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

    public String getLocation() {
        return location;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
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

    public CollegeStatus getStatus() {
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public void setStatus(CollegeStatus status) {
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
        return status == CollegeStatus.ACTIVE;
    }

    public boolean isPending() {
        return status == CollegeStatus.PENDING;
    }

    public boolean isSuspended() {
        return status == CollegeStatus.SUSPENDED;
    }

    public boolean isArchived() {
        return status == CollegeStatus.ARCHIVED;
    }
}