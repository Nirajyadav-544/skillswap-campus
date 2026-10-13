package com.skillswap.notification.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.notification.dto.NotificationPreferenceRequest;
import com.skillswap.notification.dto.NotificationPreferenceResponse;
import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationPreference;
import com.skillswap.notification.entity.NotificationType;
import com.skillswap.notification.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public NotificationPreferenceService(
            NotificationPreferenceRepository preferenceRepository,
            UserRepository userRepository
    ) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationPreferenceResponse> getMyPreferences(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return preferenceRepository.findByUser(user)
                .stream()
                .map(NotificationPreferenceResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreference(
            String userEmail,
            NotificationType notificationType,
            NotificationChannel channel
    ) {
        User user = getUserByEmail(userEmail);

        if (notificationType == null) {
            throw new BadRequestException(
                    "Notification type is required"
            );
        }

        if (channel == null) {
            throw new BadRequestException(
                    "Notification channel is required"
            );
        }

        NotificationPreference preference =
                preferenceRepository
                        .findByUserAndNotificationTypeAndChannel(
                                user,
                                notificationType,
                                channel
                        )
                        .orElseGet(() ->
                                createDefaultPreference(
                                        user,
                                        notificationType,
                                        channel
                                )
                        );

        return NotificationPreferenceResponse.from(preference);
    }

    public NotificationPreferenceResponse createOrUpdate(
            NotificationPreferenceRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Notification preference request is required"
            );
        }

        User user = getUserByEmail(userEmail);

        if (request.getNotificationType() == null) {
            throw new BadRequestException(
                    "Notification type is required"
            );
        }

        if (request.getChannel() == null) {
            throw new BadRequestException(
                    "Notification channel is required"
            );
        }

        NotificationPreference preference =
                preferenceRepository
                        .findByUserAndNotificationTypeAndChannel(
                                user,
                                request.getNotificationType(),
                                request.getChannel()
                        )
                        .orElseGet(() ->
                                new NotificationPreference(
                                        user,
                                        request.getNotificationType(),
                                        request.getChannel()
                                )
                        );

        preference.setEnabled(request.isEnabled());

        return NotificationPreferenceResponse.from(
                preferenceRepository.save(preference)
        );
    }

    public NotificationPreferenceResponse enable(
            NotificationType notificationType,
            NotificationChannel channel,
            String userEmail
    ) {
        NotificationPreference preference =
                getOrCreate(
                        userEmail,
                        notificationType,
                        channel
                );

        preference.enable();

        return NotificationPreferenceResponse.from(
                preferenceRepository.save(preference)
        );
    }

    public NotificationPreferenceResponse disable(
            NotificationType notificationType,
            NotificationChannel channel,
            String userEmail
    ) {
        NotificationPreference preference =
                getOrCreate(
                        userEmail,
                        notificationType,
                        channel
                );

        preference.disable();

        return NotificationPreferenceResponse.from(
                preferenceRepository.save(preference)
        );
    }

    public void delete(
            NotificationType notificationType,
            NotificationChannel channel,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        NotificationPreference preference =
                preferenceRepository
                        .findByUserAndNotificationTypeAndChannel(
                                user,
                                notificationType,
                                channel
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Notification preference not found"
                                )
                        );

        preferenceRepository.delete(preference);
    }

    @Transactional(readOnly = true)
    public boolean isEnabled(
            String userEmail,
            NotificationType notificationType,
            NotificationChannel channel
    ) {
        User user = getUserByEmail(userEmail);

        return preferenceRepository
                .existsByUserAndNotificationTypeAndChannelAndEnabledTrue(
                        user,
                        notificationType,
                        channel
                );
    }

    private NotificationPreference getOrCreate(
            String userEmail,
            NotificationType notificationType,
            NotificationChannel channel
    ) {
        User user = getUserByEmail(userEmail);

        if (notificationType == null) {
            throw new BadRequestException(
                    "Notification type is required"
            );
        }

        if (channel == null) {
            throw new BadRequestException(
                    "Notification channel is required"
            );
        }

        return preferenceRepository
                .findByUserAndNotificationTypeAndChannel(
                        user,
                        notificationType,
                        channel
                )
                .orElseGet(() ->
                        createDefaultPreference(
                                user,
                                notificationType,
                                channel
                        )
                );
    }

    private NotificationPreference createDefaultPreference(
            User user,
            NotificationType notificationType,
            NotificationChannel channel
    ) {
        NotificationPreference preference =
                new NotificationPreference(
                        user,
                        notificationType,
                        channel
                );

        return preferenceRepository.save(preference);
    }

    private User getUserByEmail(
            String userEmail
    ) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }
}