package com.skillswap.chat.controller;

import com.skillswap.chat.dto.MessageResponse;
import com.skillswap.chat.dto.WebSocketSendMessageRequest;
import com.skillswap.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketMessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketMessageController(
            MessageService messageService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(
            WebSocketSendMessageRequest request,
            Principal principal
    ) {

        if (principal == null) {
            throw new IllegalArgumentException(
                    "WebSocket user is not authenticated"
            );
        }

        if (request.getConversationId() == null) {
            throw new IllegalArgumentException(
                    "Conversation ID is required"
            );
        }

        MessageResponse response =
                messageService.sendMessage(
                        request.getConversationId(),
                        request,
                        principal.getName()
                );

        messagingTemplate.convertAndSend(
                "/topic/conversations/"
                        + request.getConversationId(),
                response
        );
    }
}