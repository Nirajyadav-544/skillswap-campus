package com.skillswap.notification.controller;

import com.skillswap.notification.dto.NotificationPageResponse;
import com.skillswap.notification.dto.NotificationSearchRequest;
import com.skillswap.notification.entity.NotificationStatus;
import com.skillswap.notification.entity.NotificationType;
import com.skillswap.notification.service.NotificationDiscoveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/discovery")
public class NotificationDiscoveryController {

    private final NotificationDiscoveryService notificationDiscoveryService;

    public NotificationDiscoveryController(
            NotificationDiscoveryService notificationDiscoveryService
    ) {
        this.notificationDiscoveryService = notificationDiscoveryService;
    }

    @GetMapping
    public ResponseEntity<NotificationPageResponse> search(
            @Valid NotificationSearchRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationDiscoveryService.search(
                        request,
                        userEmail
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<NotificationPageResponse> searchByStatus(
            @PathVariable NotificationStatus status,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                notificationDiscoveryService.searchByStatus(
                        status,
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<NotificationPageResponse> searchByType(
            @PathVariable NotificationType type,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                notificationDiscoveryService.searchByType(
                        type,
                        userEmail,
                        page,
                        size
                )
        );
    }
}