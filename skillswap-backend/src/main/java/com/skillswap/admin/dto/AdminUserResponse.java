package com.skillswap.admin.dto;

import com.skillswap.identity.entity.Role;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.entity.VerificationMethod;

import java.time.LocalDateTime;

public class AdminUserResponse {

    private Long id;
    private String email;
    private String phoneNumber;

    private String firstName;
    private String lastName;

    private boolean enabled;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean accountVerified;

    private Role role;
    private VerificationMethod verificationMethod;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminUserResponse() {
    }

    public static AdminUserResponse from(User user) {

        AdminUserResponse response = new AdminUserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());

        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());

        response.setEnabled(user.isEnabled());
        response.setEmailVerified(user.isEmailVerified());
        response.setPhoneVerified(user.isPhoneVerified());
        response.setAccountVerified(user.isAccountVerified());

        response.setRole(user.getRole());
        response.setVerificationMethod(user.getVerificationMethod());

        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public boolean isAccountVerified() {
        return accountVerified;
    }

    public void setAccountVerified(boolean accountVerified) {
        this.accountVerified = accountVerified;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public VerificationMethod getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(VerificationMethod verificationMethod) {
        this.verificationMethod = verificationMethod;
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