package com.skillswap.identity.dto;

import com.skillswap.identity.entity.VerificationMethod;
import jakarta.validation.constraints.NotNull;

public record ResendOtpRequest(

        @NotNull
        VerificationMethod method
) {
}