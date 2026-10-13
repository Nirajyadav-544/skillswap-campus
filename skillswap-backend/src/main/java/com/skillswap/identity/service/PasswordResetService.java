
package com.skillswap.identity.service;

import com.skillswap.identity.entity.PasswordResetToken;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.PasswordResetTokenRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetTokenRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String createResetToken(
            User user
    ) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        repository.deleteAllByUser(user);

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setUser(user);

        resetToken.setToken(
                UUID.randomUUID().toString()
                        + UUID.randomUUID()
        );

        LocalDateTime now =
                LocalDateTime.now();

        resetToken.setCreatedAt(now);

        resetToken.setExpiresAt(
                now.plusMinutes(10)
        );

        resetToken.setUsed(false);

        PasswordResetToken savedToken =
                repository.save(resetToken);

        return savedToken.getToken();
    }

    @Transactional(readOnly = true)
    public User getUserByResetToken(
            String token
    ) {

        if (token == null ||
                token.isBlank()) {

            throw new IllegalArgumentException(
                    "Password reset token is required"
            );
        }

        PasswordResetToken resetToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid password reset token"
                                )
                        );

        if (!resetToken.isUsable()) {
            throw new IllegalArgumentException(
                    "Password reset token is expired or already used"
            );
        }

        return resetToken.getUser();
    }

    @Transactional
    public void resetPassword(
            String token,
            String newPassword
    ) {

        if (token == null ||
                token.isBlank()) {

            throw new IllegalArgumentException(
                    "Password reset token is required"
            );
        }

        if (newPassword == null ||
                newPassword.isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        PasswordResetToken resetToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid password reset token"
                                )
                        );

        if (!resetToken.isUsable()) {
            throw new IllegalArgumentException(
                    "Password reset token is expired or already used"
            );
        }

        User user =
                resetToken.getUser();

        if (passwordEncoder.matches(
                newPassword,
                user.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "New password must be different from your current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        resetToken.setUsed(true);

        repository.save(resetToken);
    }
}

