package com.skillswap.notification.controller;

import com.skillswap.notification.dto.NotificationPreferenceRequest;
import com.skillswap.notification.dto.NotificationPreferenceResponse;
import com.skillswap.notification.entity.NotificationChannel;
import com.skillswap.notification.entity.NotificationType;
import com.skillswap.notification.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;

    public NotificationPreferenceController(
            NotificationPreferenceService notificationPreferenceService
    ) {
        this.notificationPreferenceService = notificationPreferenceService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationPreferenceResponse>> getMyPreferences(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationPreferenceService.getMyPreferences(userEmail)
        );
    }

    @GetMapping("/one")
    public ResponseEntity<NotificationPreferenceResponse> getPreference(
            @RequestParam String userEmail,
            @RequestParam NotificationType notificationType,
            @RequestParam NotificationChannel channel
    ) {
        return ResponseEntity.ok(
                notificationPreferenceService.getPreference(
                        userEmail,
                        notificationType,
                        channel
                )
        );
    }

    @PostMapping
    public ResponseEntity<NotificationPreferenceResponse> createOrUpdate(
            @Valid @RequestBody NotificationPreferenceRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                notificationPreferenceService.createOrUpdate(
                        request,
                        userEmail
                )
        );
    }

    @PatchMapping("/enable")
    public ResponseEntity<NotificationPreferenceResponse> enable(
            @RequestParam String userEmail,
            @RequestParam NotificationType notificationType,
            @RequestParam NotificationChannel channel
    ) {
        return ResponseEntity.ok(
                notificationPreferenceService.enable(
                        notificationType,
                        channel,
                        userEmail
                )
        );
    }

    @PatchMapping("/disable")
    public ResponseEntity<NotificationPreferenceResponse> disable(
            @RequestParam String userEmail,
            @RequestParam NotificationType notificationType,
            @RequestParam NotificationChannel channel
    ) {
        return ResponseEntity.ok(
                notificationPreferenceService.disable(
                        notificationType,
                        channel,
                        userEmail
                )
        );
    }

    @GetMapping("/enabled")
    public ResponseEntity<Boolean> isEnabled(
            @RequestParam String userEmail,
            @RequestParam NotificationType notificationType,
            @RequestParam NotificationChannel channel
    ) {
        return ResponseEntity.ok(
                notificationPreferenceService.isEnabled(
                        userEmail,
                        notificationType,
                        channel
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String userEmail,
            @RequestParam NotificationType notificationType,
            @RequestParam NotificationChannel channel
    ) {
        notificationPreferenceService.delete(
                notificationType,
                channel,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}