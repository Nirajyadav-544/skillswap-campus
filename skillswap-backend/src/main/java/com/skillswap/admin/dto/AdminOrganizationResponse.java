package com.skillswap.admin.dto;

import com.skillswap.company.entity.Company;

import java.time.LocalDateTime;

public class AdminOrganizationResponse {

    private Long id;
    private String name;
    private String legalName;
    private String description;
    private String website;
    private String industry;
    private String companySize;
    private String headquarters;
    private String logoUrl;
    private String email;
    private String phoneNumber;
    private String status;
    private Boolean verified;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminOrganizationResponse() {
    }

    public static AdminOrganizationResponse from(Company company) {

        AdminOrganizationResponse response =
                new AdminOrganizationResponse();

        response.setId(company.getId());
        response.setName(company.getName());
        response.setLegalName(company.getLegalName());
        response.setDescription(company.getDescription());
        response.setWebsite(company.getWebsite());
        response.setIndustry(company.getIndustry());
        response.setCompanySize(company.getCompanySize());
        response.setHeadquarters(company.getHeadquarters());
        response.setLogoUrl(company.getLogoUrl());
        response.setEmail(company.getEmail());
        response.setPhoneNumber(company.getPhoneNumber());

        response.setStatus(
                company.getStatus() != null
                        ? company.getStatus().name()
                        : null
        );

        response.setVerified(company.getVerified());

        response.setOwnerId(
                company.getOwner() != null
                        ? company.getOwner().getId()
                        : null
        );

        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}