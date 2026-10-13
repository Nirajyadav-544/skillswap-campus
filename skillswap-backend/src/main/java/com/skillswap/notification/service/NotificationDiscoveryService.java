package com.skillswap.notification.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.notification.dto.NotificationPageResponse;
import com.skillswap.notification.dto.NotificationSearchRequest;
import com.skillswap.notification.dto.NotificationSummaryResponse;
import com.skillswap.notification.entity.Notification;
import com.skillswap.notification.entity.NotificationStatus;
import com.skillswap.notification.entity.NotificationType;
import com.skillswap.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NotificationDiscoveryService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationDiscoveryService(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationPageResponse search(
            NotificationSearchRequest request,
            String userEmail
    ) {
        User user = getUserByEmail(userEmail);

        NotificationSearchRequest searchRequest =
                request == null
                        ? new NotificationSearchRequest()
                        : request;

        searchRequest.normalize();

        PageRequest pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        Page<Notification> notifications;

        if (Boolean.TRUE.equals(searchRequest.getUnreadOnly())) {

            List<Notification> filteredNotifications =
                    notificationRepository
                            .findByUserAndStatusOrderByCreatedAtDesc(
                                    user,
                                    NotificationStatus.UNREAD
                            )
                            .stream()
                            .filter(notification ->
                                    matchesType(
                                            notification,
                                            searchRequest
                                    ))
                            .filter(notification ->
                                    matchesKeyword(
                                            notification,
                                            searchRequest
                                    ))
                            .toList();

            int totalElements = filteredNotifications.size();

            int start = searchRequest.getPage()
                    * searchRequest.getSize();

            int end = Math.min(
                    start + searchRequest.getSize(),
                    totalElements
            );

            List<Notification> pageContent;

            if (start >= totalElements) {
                pageContent = List.of();
            } else {
                pageContent =
                        filteredNotifications.subList(
                                start,
                                end
                        );
            }

            notifications = new PageImpl<>(
                    pageContent,
                    pageable,
                    totalElements
            );

        } else if (searchRequest.getStatus() != null) {

            notifications =
                    notificationRepository
                            .findByUserAndStatus(
                                    user,
                                    searchRequest.getStatus(),
                                    pageable
                            );

        } else if (searchRequest.getType() != null) {

            notifications =
                    notificationRepository
                            .findByUserAndType(
                                    user,
                                    searchRequest.getType(),
                                    pageable
                            );

        } else {

            notifications =
                    notificationRepository
                            .findByUserOrderByCreatedAtDesc(
                                    user,
                                    pageable
                            );
        }

        Page<NotificationSummaryResponse> responsePage =
                notifications.map(
                        NotificationSummaryResponse::from
                );

        return NotificationPageResponse.from(responsePage);
    }

    public NotificationPageResponse searchByStatus(
            NotificationStatus status,
            String userEmail,
            int page,
            int size
    ) {
        User user = getUserByEmail(userEmail);

        if (status == null) {
            throw new BadRequestException(
                    "Notification status is required"
            );
        }

        page = normalizePage(page);
        size = normalizeSize(size);

        Page<NotificationSummaryResponse> result =
                notificationRepository
                        .findByUserAndStatus(
                                user,
                                status,
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

    public NotificationPageResponse searchByType(
            NotificationType type,
            String userEmail,
            int page,
            int size
    ) {
        User user = getUserByEmail(userEmail);

        if (type == null) {
            throw new BadRequestException(
                    "Notification type is required"
            );
        }

        page = normalizePage(page);
        size = normalizeSize(size);

        Page<NotificationSummaryResponse> result =
                notificationRepository
                        .findByUserAndType(
                                user,
                                type,
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

    private boolean matchesType(
            Notification notification,
            NotificationSearchRequest request
    ) {
        return request.getType() == null
                || request.getType().equals(
                        notification.getType()
                );
    }

    private boolean matchesKeyword(
            Notification notification,
            NotificationSearchRequest request
    ) {
        if (request.getKeyword() == null
                || request.getKeyword().isBlank()) {
            return true;
        }

        String keyword =
                request.getKeyword().toLowerCase();

        String title =
                notification.getTitle() == null
                        ? ""
                        : notification.getTitle().toLowerCase();

        String message =
                notification.getMessage() == null
                        ? ""
                        : notification.getMessage().toLowerCase();

        return title.contains(keyword)
                || message.contains(keyword);
    }

    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {
        if (size < 1) {
            return 20;
        }

        return Math.min(size, 100);
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
                .findByEmail(
                        userEmail.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }
}