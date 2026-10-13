package com.skillswap.company.dto;

import com.skillswap.company.entity.Company;
import com.skillswap.company.entity.CompanyStatus;

import java.time.LocalDateTime;

public class CompanyResponse {

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
    private CompanyStatus status;
    private Long ownerId;
    private Boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CompanyResponse() {
    }

    public static CompanyResponse from(Company company) {

        if (company == null) {
            return null;
        }

        CompanyResponse response = new CompanyResponse();

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
        response.setStatus(company.getStatus());
        response.setVerified(company.getVerified());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());

        if (company.getOwner() != null) {
            response.setOwnerId(company.getOwner().getId());
        }

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

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
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