package com.skillswap.notification.controller;

import com.skillswap.notification.dto.CreateNotificationRequest;
import com.skillswap.notification.dto.NotificationPageResponse;
import com.skillswap.notification.dto.NotificationResponse;
import com.skillswap.notification.dto.NotificationSummaryResponse;
import com.skillswap.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @Valid @RequestBody CreateNotificationRequest request
    ) {
        return ResponseEntity.ok(
                notificationService.create(request)
        );
    }

    @GetMapping("/{notificationId:\\d+}")
    public ResponseEntity<NotificationResponse> getById(
            @PathVariable Long notificationId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.getById(
                        notificationId,
                        userEmail
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<NotificationSummaryResponse>>
    getMyNotifications(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        userEmail
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<NotificationPageResponse>
    getMyNotificationsPage(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                notificationService.getMyNotificationsPage(
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationSummaryResponse>>
    getUnread(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications(
                        userEmail
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.countMyNotifications(
                        userEmail
                )
        );
    }

    @GetMapping("/count/unread")
    public ResponseEntity<Long> countUnread(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.countMyUnreadNotifications(
                        userEmail
                )
        );
    }

    @PatchMapping("/{notificationId:\\d+}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{notificationId:\\d+}/unread")
    public ResponseEntity<NotificationResponse> markAsUnread(
            @PathVariable Long notificationId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.markAsUnread(
                        notificationId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{notificationId:\\d+}/archive")
    public ResponseEntity<NotificationResponse> archive(
            @PathVariable Long notificationId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.archive(
                        notificationId,
                        userEmail
                )
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Integer> markAllAsRead(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.markAllAsRead(
                        userEmail
                )
        );
    }

    @PatchMapping("/archive-all")
    public ResponseEntity<Integer> archiveAll(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationService.archiveAll(
                        userEmail
                )
        );
    }

    @DeleteMapping("/{notificationId:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable Long notificationId,
            @RequestParam String userEmail
    ) {
        notificationService.delete(
                notificationId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}