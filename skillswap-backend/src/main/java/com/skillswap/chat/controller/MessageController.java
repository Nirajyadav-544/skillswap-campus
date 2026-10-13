package com.skillswap.chat.controller;

import com.skillswap.chat.dto.MessageResponse;
import com.skillswap.chat.dto.MessageSummaryResponse;
import com.skillswap.chat.dto.SendMessageRequest;
import com.skillswap.chat.dto.UpdateMessageRequest;
import com.skillswap.chat.entity.MessageStatus;
import com.skillswap.chat.entity.MessageType;
import com.skillswap.chat.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/conversations/{conversationId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(
            MessageService messageService
    ) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        messageService.sendMessage(
                                conversationId,
                                request,
                                userEmail
                        )
                );
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> getById(
            @PathVariable Long conversationId,
            @PathVariable Long messageId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.getById(
                        messageId,
                        userEmail
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<MessageSummaryResponse>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.getMessages(
                        conversationId,
                        userEmail
                )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<?> getMessagesPage(
            @PathVariable Long conversationId,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                messageService.getMessagesPage(
                        conversationId,
                        userEmail,
                        page,
                        size
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MessageSummaryResponse>> getByStatus(
            @PathVariable Long conversationId,
            @PathVariable MessageStatus status,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.getMessagesByStatus(
                        conversationId,
                        status,
                        userEmail
                )
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MessageSummaryResponse>> getByType(
            @PathVariable Long conversationId,
            @PathVariable MessageType type,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.getMessagesByType(
                        conversationId,
                        type,
                        userEmail
                )
        );
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable Long conversationId,
            @PathVariable Long messageId,
            @Valid @RequestBody UpdateMessageRequest request,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.updateMessage(
                        messageId,
                        request,
                        userEmail
                )
        );
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long conversationId,
            @PathVariable Long messageId,
            @RequestParam String userEmail
    ) {
        messageService.deleteMessage(
                messageId,
                userEmail
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{messageId}/delivered")
    public ResponseEntity<MessageResponse> markDelivered(
            @PathVariable Long conversationId,
            @PathVariable Long messageId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.markDelivered(
                        messageId,
                        userEmail
                )
        );
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<MessageResponse> markRead(
            @PathVariable Long conversationId,
            @PathVariable Long messageId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.markRead(
                        messageId,
                        userEmail
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @PathVariable Long conversationId,
            @RequestParam String userEmail
    ) {
        return ResponseEntity.ok(
                messageService.countMessages(
                        conversationId,
                        userEmail
                )
        );
    }
   
    @PatchMapping(
        "/{messageId}/delete-for-me"
)
public ResponseEntity<Void> deleteForMe(
        @PathVariable Long conversationId,
        @PathVariable Long messageId,
        @RequestParam String userEmail
) {
    messageService.deleteForMe(
            messageId,
            userEmail
    );

    return ResponseEntity.noContent().build();
}

@PatchMapping(
        "/{messageId}/star"
)
public ResponseEntity<Boolean> toggleStar(
        @PathVariable Long conversationId,
        @PathVariable Long messageId,
        @RequestParam String userEmail
) {
    return ResponseEntity.ok(
            messageService.toggleStar(
                    messageId,
                    userEmail
            )
    );
}

@GetMapping("/starred")
public ResponseEntity<List<MessageSummaryResponse>> getStarredMessages(
        @PathVariable Long conversationId,
        @RequestParam String userEmail
) {
    return ResponseEntity.ok(
            messageService.getStarredMessages(
                    conversationId,
                    userEmail
            )
    );
}

}