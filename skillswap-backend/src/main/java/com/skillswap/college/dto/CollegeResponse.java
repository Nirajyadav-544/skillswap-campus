package com.skillswap.college.dto;

import com.skillswap.college.entity.College;
import com.skillswap.college.entity.CollegeStatus;

import java.time.LocalDateTime;

public class CollegeResponse {

    private Long id;
    private String name;
    private String legalName;
    private String description;
    private String website;
    private String location;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String logoUrl;
    private String email;
    private String phoneNumber;
    private CollegeStatus status;
    private Long ownerId;
    private Boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CollegeResponse() {
    }

    public static CollegeResponse from(College college) {
        CollegeResponse response = new CollegeResponse();

        response.setId(college.getId());
        response.setName(college.getName());
        response.setLegalName(college.getLegalName());
        response.setDescription(college.getDescription());
        response.setWebsite(college.getWebsite());
        response.setLocation(college.getLocation());
        response.setCity(college.getCity());
        response.setState(college.getState());
        response.setCountry(college.getCountry());
        response.setPostalCode(college.getPostalCode());
        response.setLogoUrl(college.getLogoUrl());
        response.setEmail(college.getEmail());
        response.setPhoneNumber(college.getPhoneNumber());
        response.setStatus(college.getStatus());
        response.setOwnerId(
                college.getOwner() != null
                        ? college.getOwner().getId()
                        : null
        );
        response.setVerified(college.getVerified());
        response.setCreatedAt(college.getCreatedAt());
        response.setUpdatedAt(college.getUpdatedAt());

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public CollegeStatus getStatus() {
        return status;
    }

    public void setStatus(CollegeStatus status) {
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