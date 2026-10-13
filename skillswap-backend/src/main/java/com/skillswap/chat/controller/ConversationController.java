package com.skillswap.chat.controller;

import com.skillswap.chat.dto.CreateConversationRequest;
import com.skillswap.chat.dto.ConversationResponse;
import com.skillswap.chat.dto.ConversationSummaryResponse;
import com.skillswap.chat.entity.ConversationType;
import com.skillswap.chat.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(
            ConversationService conversationService
    ) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> create(
            @Valid @RequestBody CreateConversationRequest request,
            @RequestParam String userEmail
    ) {
        ConversationResponse response =
                conversationService.create(
                        request,
                        userEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Creates or returns an existing direct conversation
     * between the logged-in student and the target student.
     */
    @PostMapping("/direct")
    public ResponseEntity<ConversationResponse> createDirectConversation(
            @RequestParam Long targetUserId,
            @RequestParam String userEmail
    ) {
        ConversationResponse response =
                conversationService.createOrGetDirectConversation(
                        targetUserId,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationResponse> getById(
            @PathVariable Long conversationId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                conversationService.getById(
                        conversationId,
                        userEmail
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<ConversationSummaryResponse>> getMyConversations(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                conversationService.getMyConversations(
                        userEmail
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<?> getMyConversationsPage(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                conversationService.getMyConversationsPage(
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ConversationSummaryResponse>> getByType(
            @PathVariable ConversationType type,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                conversationService.getByType(
                        type,
                        userEmail
                )
        );
    }

    @PatchMapping("/{conversationId}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable Long conversationId,
            @RequestParam String userEmail
    ) {
        conversationService.activate(
                conversationId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{conversationId}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long conversationId,
            @RequestParam String userEmail
    ) {
        conversationService.deactivate(
                conversationId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{conversationId}/leave")
    public ResponseEntity<Void> leave(
            @PathVariable Long conversationId,
            @RequestParam String userEmail
    ) {
        conversationService.leave(
                conversationId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long conversationId,
            @RequestParam String userEmail
    ) {
        conversationService.delete(
                conversationId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }
}