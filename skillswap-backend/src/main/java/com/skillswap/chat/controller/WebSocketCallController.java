package com.skillswap.chat.controller;

import com.skillswap.chat.dto.CallSignalRequest;
import com.skillswap.chat.dto.CallSignalResponse;
import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationType;
import com.skillswap.chat.entity.ParticipantStatus;
import com.skillswap.chat.repository.ConversationParticipantRepository;
import com.skillswap.chat.repository.ConversationRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketCallController {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketCallController(
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/call.signal")
    public void signalCall(
            @Valid CallSignalRequest request,
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

        if (request.getTargetUserId() == null) {
            throw new IllegalArgumentException(
                    "Target user ID is required"
            );
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException(
                    "Call signal type is required"
            );
        }

        User sender = userRepository.findByEmail(
                principal.getName()
                        .trim()
                        .toLowerCase()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "WebSocket user not found"
                )
        );

        Conversation conversation =
                conversationRepository.findById(
                        request.getConversationId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Conversation not found"
                        )
                );

        if (!conversation.getActive()) {
            throw new IllegalArgumentException(
                    "Conversation is not active"
            );
        }

        if (conversation.getType() != ConversationType.DIRECT) {
            throw new IllegalArgumentException(
                    "Voice and video calls are currently available only for direct conversations"
            );
        }

        validateActiveParticipant(
                request.getConversationId(),
                sender.getId()
        );

        if (sender.getId().equals(
                request.getTargetUserId()
        )) {
            throw new IllegalArgumentException(
                    "You cannot call yourself"
            );
        }

        User targetUser = userRepository.findById(
                request.getTargetUserId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Target user not found"
                )
        );

        validateActiveParticipant(
                request.getConversationId(),
                targetUser.getId()
        );

        CallSignalResponse response =
                new CallSignalResponse(
                        request.getConversationId(),
                        sender.getId(),
                        targetUser.getId(),
                        request.getType(),
                        request.getPayload()
                );

        messagingTemplate.convertAndSendToUser(
                targetUser.getEmail(),
                "/queue/calls",
                response
        );
    }

    private void validateActiveParticipant(
            Long conversationId,
            Long userId
    ) {

        boolean activeParticipant =
                participantRepository
                        .existsByConversationIdAndUserIdAndStatus(
                                conversationId,
                                userId,
                                ParticipantStatus.ACTIVE
                        );

        if (!activeParticipant) {
            throw new IllegalArgumentException(
                    "User is not an active participant in this conversation"
            );
        }
    }
}