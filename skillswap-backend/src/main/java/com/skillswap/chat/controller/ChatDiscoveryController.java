package com.skillswap.chat.controller;

import com.skillswap.chat.dto.ChatPageResponse;
import com.skillswap.chat.dto.ChatSearchRequest;
import com.skillswap.chat.dto.ConversationSummaryResponse;
import com.skillswap.chat.dto.MessageSummaryResponse;
import com.skillswap.chat.service.ChatDiscoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/discovery")
public class ChatDiscoveryController {

    private final ChatDiscoveryService chatDiscoveryService;

    public ChatDiscoveryController(
            ChatDiscoveryService chatDiscoveryService
    ) {
        this.chatDiscoveryService = chatDiscoveryService;
    }

    /*
     * Search user's conversations
     *
     * Example:
     * GET /api/v1/chat/discovery/conversations?userEmail=test@gmail.com
     *
     * With filters:
     * GET /api/v1/chat/discovery/conversations
     *     ?userEmail=test@gmail.com
     *     &keyword=project
     *     &page=0
     *     &size=20
     */
    @GetMapping("/conversations")
    public ResponseEntity<ChatPageResponse<ConversationSummaryResponse>> searchConversations(
            @ModelAttribute ChatSearchRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                chatDiscoveryService.searchConversations(
                        request,
                        userEmail
                )
        );
    }

    /*
     * Search messages
     *
     * Example:
     * GET /api/v1/chat/discovery/messages?userEmail=test@gmail.com
     *
     * With filters:
     * GET /api/v1/chat/discovery/messages
     *     ?userEmail=test@gmail.com
     *     &keyword=hello
     *     &conversationId=1
     *     &page=0
     *     &size=20
     */
    @GetMapping("/messages")
    public ResponseEntity<ChatPageResponse<MessageSummaryResponse>> searchMessages(
            @ModelAttribute ChatSearchRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                chatDiscoveryService.searchMessages(
                        request,
                        userEmail
                )
        );
    }

    /*
     * Get all active conversations of the current user
     *
     * Example:
     * GET /api/v1/chat/discovery/my-conversations?userEmail=test@gmail.com
     */
    @GetMapping("/my-conversations")
    public ResponseEntity<List<ConversationSummaryResponse>> getMyConversations(
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                chatDiscoveryService.getMyConversations(
                        userEmail
                )
        );
    }

    /*
     * Search messages inside a specific conversation
     *
     * Example:
     * GET /api/v1/chat/discovery/conversations/1/messages
     *     ?userEmail=test@gmail.com
     *     &keyword=hello
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageSummaryResponse>> searchConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(required = false) String keyword,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                chatDiscoveryService.searchConversationMessages(
                        conversationId,
                        keyword,
                        userEmail
                )
        );
    }
}