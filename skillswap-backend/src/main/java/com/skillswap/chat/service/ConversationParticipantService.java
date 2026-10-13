package com.skillswap.chat.service;

import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationParticipant;
import com.skillswap.chat.entity.ParticipantStatus;
import com.skillswap.chat.repository.ConversationParticipantRepository;
import com.skillswap.chat.repository.ConversationRepository;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConversationParticipantService {

    private final ConversationParticipantRepository participantRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationParticipantService(
            ConversationParticipantRepository participantRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository
    ) {
        this.participantRepository = participantRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    public ConversationParticipant addParticipant(
            Long conversationId,
            Long userId,
            String currentUserEmail
    ) {
        Conversation conversation = getConversation(conversationId);

        validateActiveParticipant(conversation, currentUserEmail);

        User user = getUser(userId);

        if (participantRepository.existsByConversationIdAndUserId(
                conversationId,
                userId
        )) {
            throw new BadRequestException(
                    "User is already a participant in this conversation"
            );
        }

        ConversationParticipant participant =
                new ConversationParticipant(conversation, user);

        return participantRepository.save(participant);
    }

    public ConversationParticipant activateParticipant(
            Long conversationId,
            Long userId,
            String currentUserEmail
    ) {
        Conversation conversation = getConversation(conversationId);

        validateActiveParticipant(conversation, currentUserEmail);

        ConversationParticipant participant =
                getParticipant(conversationId, userId);

        if (participant.isBlocked() || participant.isRemoved()) {
            throw new BadRequestException(
                    "This participant cannot be activated"
            );
        }

        participant.activate();

        return participantRepository.save(participant);
    }

    public ConversationParticipant removeParticipant(
            Long conversationId,
            Long userId,
            String currentUserEmail
    ) {
        Conversation conversation = getConversation(conversationId);

        validateActiveParticipant(conversation, currentUserEmail);

        ConversationParticipant participant =
                getParticipant(conversationId, userId);

        User currentUser = getUser(currentUserEmail);

        if (participant.getUser().getId()
                .equals(currentUser.getId())) {
            throw new BadRequestException(
                    "You cannot remove yourself. Use leave instead."
            );
        }

        participant.remove();

        return participantRepository.save(participant);
    }

    public ConversationParticipant leaveConversation(
            Long conversationId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        ConversationParticipant participant =
                getParticipant(conversationId, user.getId());

        if (participant.isRemoved() || participant.isBlocked()) {
            throw new BadRequestException(
                    "You cannot leave this conversation"
            );
        }

        participant.leave();

        return participantRepository.save(participant);
    }

    public ConversationParticipant blockParticipant(
            Long conversationId,
            Long userId,
            String currentUserEmail
    ) {
        Conversation conversation = getConversation(conversationId);

        validateActiveParticipant(conversation, currentUserEmail);

        ConversationParticipant participant =
                getParticipant(conversationId, userId);

        User currentUser = getUser(currentUserEmail);

        if (participant.getUser().getId()
                .equals(currentUser.getId())) {
            throw new BadRequestException(
                    "You cannot block yourself"
            );
        }

        participant.block();

        return participantRepository.save(participant);
    }

    public ConversationParticipant markRead(
            Long conversationId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        ConversationParticipant participant =
                getParticipant(conversationId, user.getId());

        if (!participant.isActive()) {
            throw new BadRequestException(
                    "Only active participants can mark messages as read"
            );
        }

        participant.markRead();

        return participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public List<ConversationParticipant> getParticipants(
            Long conversationId,
            String currentUserEmail
    ) {
        Conversation conversation = getConversation(conversationId);

        validateActiveParticipant(conversation, currentUserEmail);

        return participantRepository.findByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public ConversationParticipant getParticipant(
            Long conversationId,
            Long userId
    ) {
        return participantRepository
                .findByConversationIdAndUserId(
                        conversationId,
                        userId
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation participant not found"
                ));
    }

    @Transactional(readOnly = true)
    public boolean isActiveParticipant(
            Long conversationId,
            Long userId
    ) {
        return participantRepository
                .existsByConversationIdAndUserIdAndStatus(
                        conversationId,
                        userId,
                        ParticipantStatus.ACTIVE
                );
    }

    private Conversation getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation not found: " + conversationId
                ));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + userId
                ));
    }

    private User getUser(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("User email is required");
        }

        return userRepository.findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email
                ));
    }

    private void validateActiveParticipant(
            Conversation conversation,
            String userEmail
    ) {
        User user = getUser(userEmail);

        boolean active =
                participantRepository
                        .existsByConversationIdAndUserIdAndStatus(
                                conversation.getId(),
                                user.getId(),
                                ParticipantStatus.ACTIVE
                        );

        if (!active) {
            throw new BadRequestException(
                    "You are not an active participant in this conversation"
            );
        }
    }
}