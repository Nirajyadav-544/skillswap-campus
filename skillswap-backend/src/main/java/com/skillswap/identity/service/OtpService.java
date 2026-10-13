
package com.skillswap.identity.service;

import com.skillswap.identity.entity.OtpVerification;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.entity.VerificationMethod;
import com.skillswap.identity.repository.OtpVerificationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public OtpService(
            OtpVerificationRepository otpVerificationRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.otpRepository = otpVerificationRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    // SEND OTP
    // ============================================================

    @Transactional
    public void sendOtp(
            User user,
            VerificationMethod method
    ) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        if (method != VerificationMethod.EMAIL) {
            throw new IllegalArgumentException(
                    "Only email verification is supported"
            );
        }

        String target = getTarget(user);

        OtpVerification previous =
                otpRepository
                        .findTopByUserAndMethodAndTargetAndUsedFalseOrderByCreatedAtDesc(
                                user,
                                VerificationMethod.EMAIL,
                                target
                        )
                        .orElse(null);

        if (previous != null) {

            long seconds =
                    Duration.between(
                            previous.getLastSentAt(),
                            LocalDateTime.now()
                    ).getSeconds();

            if (seconds < RESEND_COOLDOWN_SECONDS) {
                throw new IllegalStateException(
                        "Please wait before requesting another OTP"
                );
            }
        }

        String otp = generateOtp();

        LocalDateTime now =
                LocalDateTime.now();

        OtpVerification verification =
                new OtpVerification();

        verification.setUser(user);

        verification.setMethod(
                VerificationMethod.EMAIL
        );

        verification.setTarget(target);

        /*
         * Never store the actual OTP.
         * Store only its BCrypt hash.
         */
        verification.setOtpHash(
                passwordEncoder.encode(otp)
        );

        verification.setCreatedAt(now);

        verification.setLastSentAt(now);

        verification.setExpiresAt(
                now.plusMinutes(
                        OTP_EXPIRY_MINUTES
                )
        );

        verification.setAttempts(0);

        verification.setMaxAttempts(
                MAX_ATTEMPTS
        );

        verification.setUsed(false);

        if (previous != null) {

            verification.setResendCount(
                    previous.getResendCount() + 1
            );

        } else {

            verification.setResendCount(0);
        }

        otpRepository.save(verification);

        /*
         * Send OTP through Gmail.
         */
        emailService.sendOtp(
                target,
                otp
        );
    }

    // ============================================================
    // REGISTRATION OTP VERIFICATION
    // ============================================================

    @Transactional
    public void verifyOtp(
            User user,
            VerificationMethod method,
            String otp
    ) {

        validateUserAndMethod(
                user,
                method
        );

        validateOtpFormat(otp);

        String target =
                getTarget(user);

        OtpVerification verification =
                findActiveOtp(
                        user,
                        target
                );

        validateOtpState(verification);

        /*
         * Count every verification attempt.
         */
        verification.setAttempts(
                verification.getAttempts() + 1
        );

        /*
         * Compare entered OTP with stored BCrypt hash.
         */
        if (!passwordEncoder.matches(
                otp,
                verification.getOtpHash()
        )) {

            otpRepository.save(verification);

            throw new IllegalArgumentException(
                    "Invalid OTP"
            );
        }

        /*
         * OTP successfully verified.
         */
        verification.setUsed(true);

        otpRepository.save(verification);

        /*
         * Registration OTP verification:
         * mark the user's email/account as verified.
         */
        user.setEmailVerified(true);

        user.setAccountVerified(true);
    }

    // ============================================================
    // PASSWORD RESET OTP VERIFICATION
    // ============================================================

    @Transactional
    public void verifyPasswordResetOtp(
            User user,
            VerificationMethod method,
            String otp
    ) {

        validateUserAndMethod(
                user,
                method
        );

        validateOtpFormat(otp);

        String target =
                getTarget(user);

        OtpVerification verification =
                findActiveOtp(
                        user,
                        target
                );

        validateOtpState(verification);

        /*
         * Count every password-reset verification attempt.
         */
        verification.setAttempts(
                verification.getAttempts() + 1
        );

        /*
         * Compare entered OTP with stored BCrypt hash.
         */
        if (!passwordEncoder.matches(
                otp,
                verification.getOtpHash()
        )) {

            otpRepository.save(verification);

            throw new IllegalArgumentException(
                    "Invalid OTP"
            );
        }

        /*
         * OTP is valid for password reset.
         *
         * IMPORTANT:
         * We DO NOT set emailVerified/accountVerified here.
         * This OTP only authorizes the password-reset flow.
         */
        verification.setUsed(true);

        otpRepository.save(verification);
    }

    // ============================================================
    // VALIDATION HELPERS
    // ============================================================

    private void validateUserAndMethod(
            User user,
            VerificationMethod method
    ) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        if (method != VerificationMethod.EMAIL) {
            throw new IllegalArgumentException(
                    "Only email verification is supported"
            );
        }
    }

    private void validateOtpFormat(
            String otp
    ) {

        if (otp == null || otp.isBlank()) {
            throw new IllegalArgumentException(
                    "OTP is required"
            );
        }

        if (!otp.matches(
                "\\d{" + OTP_LENGTH + "}"
        )) {

            throw new IllegalArgumentException(
                    "OTP must be a 6-digit number"
            );
        }
    }

    private OtpVerification findActiveOtp(
            User user,
            String target
    ) {

        return otpRepository
                .findTopByUserAndMethodAndTargetAndUsedFalseOrderByCreatedAtDesc(
                        user,
                        VerificationMethod.EMAIL,
                        target
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No active OTP found"
                        )
                );
    }

    private void validateOtpState(
            OtpVerification verification
    ) {

        if (verification.isExpired()) {
            throw new IllegalStateException(
                    "OTP has expired"
            );
        }

        if (verification.hasExceededAttempts()) {
            throw new IllegalStateException(
                    "Maximum OTP attempts exceeded"
            );
        }
    }

    // ============================================================
    // USER EMAIL
    // ============================================================

    private String getTarget(
            User user
    ) {

        if (user.getEmail() == null ||
                user.getEmail().isBlank()) {

            throw new IllegalStateException(
                    "Email is required"
            );
        }

        return user.getEmail()
                .trim()
                .toLowerCase();
    }

    // ============================================================
    // OTP GENERATION
    // ============================================================

    private String generateOtp() {

        int min = 100000;
        int max = 999999;

        return String.valueOf(
                secureRandom.nextInt(
                        max - min + 1
                ) + min
        );
    }
}

