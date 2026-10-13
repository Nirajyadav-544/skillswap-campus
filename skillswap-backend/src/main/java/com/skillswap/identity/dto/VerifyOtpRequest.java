package com.skillswap.identity.dto;

import com.skillswap.identity.entity.VerificationMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerifyOtpRequest(

        @NotBlank
        @Size(min = 6, max = 6)
        String otp,

        @NotNull
        VerificationMethod method
) {
}