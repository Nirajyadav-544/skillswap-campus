package com.skillswap.notification.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.notification.dto.CreateNotificationRequest;
import com.skillswap.notification.dto.NotificationPageResponse;
import com.skillswap.notification.dto.NotificationResponse;
import com.skillswap.notification.dto.NotificationSummaryResponse;
import com.skillswap.notification.entity.Notification;
import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationStatus;
import com.skillswap.notification.entity.NotificationType;
import com.skillswap.notification.repository.NotificationPreferenceRepository;
import com.skillswap.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationPreferenceRepository notificationPreferenceRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.userRepository = userRepository;
    }

    public NotificationResponse create(
            CreateNotificationRequest request
    ) {
        if (request == null) {
            throw new BadRequestException("Notification request is required");
        }

        request.normalize();

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new BadRequestException("User not found")
                );

        NotificationChannel channel = request.getChannel();

        if (channel == null) {
            channel = NotificationChannel.IN_APP;
        }

        boolean enabled =
                notificationPreferenceRepository
                        .existsByUserAndNotificationTypeAndChannelAndEnabledTrue(
                                user,
                                request.getType(),
                                channel
                        );

        boolean preferenceExists =
                notificationPreferenceRepository
                        .existsByUserAndNotificationTypeAndChannel(
                                user,
                                request.getType(),
                                channel
                        );

        /*
         * If the user has explicitly configured this notification/channel,
         * respect that preference.
         *
         * If no preference exists, notifications remain enabled by default.
         */
        if (preferenceExists && !enabled) {
            throw new BadRequestException(
                    "Notification is disabled for this user"
            );
        }

        Notification notification = new Notification(
                user,
                request.getType(),
                request.getTitle(),
                request.getMessage()
        );

        notification.setChannel(channel);
        notification.setReferenceType(request.getReferenceType());
        notification.setReferenceId(request.getReferenceId());
        notification.setActionUrl(request.getActionUrl());
        notification.setExpiresAt(request.getExpiresAt());

        notification.normalize();

        Notification saved = notificationRepository.save(notification);

        return NotificationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(
            Long notificationId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new BadRequestException("Notification not found")
                );

        validateOwner(notification, user);

        return NotificationResponse.from(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationSummaryResponse> getMyNotifications(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user, PageRequest.of(
                        0,
                        100,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                ))
                .getContent()
                .stream()
                .map(NotificationSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationPageResponse getMyNotificationsPage(
            String userEmail,
            int page,
            int size
    ) {
        User user = getUserByEmail(userEmail);

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        Page<NotificationSummaryResponse> result =
                notificationRepository
                        .findByUserOrderByCreatedAtDesc(
                                user,
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by(
                                                Sort.Direction.DESC,
                                                "createdAt"
                                        )
                                )
                        )
                        .map(NotificationSummaryResponse::from);

        return NotificationPageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public List<NotificationSummaryResponse> getMyUnreadNotifications(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return notificationRepository
                .findByUserAndStatusOrderByCreatedAtDesc(
                        user,
                        NotificationStatus.UNREAD
                )
                .stream()
                .map(NotificationSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countMyNotifications(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return notificationRepository.countByUser(user);
    }

    @Transactional(readOnly = true)
    public long countMyUnreadNotifications(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        return notificationRepository.countByUserAndStatus(
                user,
                NotificationStatus.UNREAD
        );
    }

    public NotificationResponse markAsRead(
            Long notificationId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Notification notification = getNotification(
                notificationId
        );

        validateOwner(notification, user);

        notification.markAsRead();

        return NotificationResponse.from(
                notificationRepository.save(notification)
        );
    }

    public NotificationResponse markAsUnread(
            Long notificationId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Notification notification = getNotification(
                notificationId
        );

        validateOwner(notification, user);

        notification.markAsUnread();

        return NotificationResponse.from(
                notificationRepository.save(notification)
        );
    }

    public NotificationResponse archive(
            Long notificationId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Notification notification = getNotification(
                notificationId
        );

        validateOwner(notification, user);

        notification.archive();

        return NotificationResponse.from(
                notificationRepository.save(notification)
        );
    }

    public void delete(
            Long notificationId,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        Notification notification = getNotification(
                notificationId
        );

        validateOwner(notification, user);

        notification.delete();

        notificationRepository.save(notification);
    }

    public int markAllAsRead(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndStatusOrderByCreatedAtDesc(
                                user,
                                NotificationStatus.UNREAD
                        );

        for (Notification notification : notifications) {
            notification.markAsRead();
        }

        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    public int archiveAll(
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndStatusOrderByCreatedAtDesc(
                                user,
                                NotificationStatus.READ
                        );

        for (Notification notification : notifications) {
            notification.archive();
        }

        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    private Notification getNotification(
            Long notificationId
    ) {
        if (notificationId == null) {
            throw new BadRequestException(
                    "Notification id is required"
            );
        }

        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Notification not found"
                        )
                );
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

    private void validateOwner(
            Notification notification,
            User user
    ) {
        if (notification.getUser() == null ||
                user == null ||
                notification.getUser().getId() == null ||
                user.getId() == null ||
                !notification.getUser().getId().equals(user.getId())) {

            throw new BadRequestException(
                    "You are not authorized to access this notification"
            );
        }
    }
}