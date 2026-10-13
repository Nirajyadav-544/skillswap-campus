package com.skillswap.identity.dto;

import com.skillswap.identity.entity.VerificationMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 100,
                message = "Password must be at least 8 characters long"
        )
        String password,

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Pattern(
                regexp = "^\\+?[1-9]\\d{7,14}$",
                message = "Invalid phone number"
        )
        String phoneNumber,

        @NotNull(message = "Verification method is required")
        VerificationMethod verificationMethod
) {
}