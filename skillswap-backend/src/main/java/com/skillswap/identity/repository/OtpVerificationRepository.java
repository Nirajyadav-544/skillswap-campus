package com.skillswap.identity.repository;

import com.skillswap.identity.entity.OtpVerification;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.entity.VerificationMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification>
    findTopByUserAndMethodAndTargetAndUsedFalseOrderByCreatedAtDesc(
            User user,
            VerificationMethod method,
            String target
    );

    Optional<OtpVerification>
    findTopByUserAndMethodAndUsedFalseOrderByCreatedAtDesc(
            User user,
            VerificationMethod method
    );

    Optional<OtpVerification>
    findTopByTargetAndMethodAndUsedFalseOrderByCreatedAtDesc(
            String target,
            VerificationMethod method
    );
}