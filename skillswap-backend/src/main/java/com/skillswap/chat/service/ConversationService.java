package com.skillswap.chat.service;

import com.skillswap.chat.dto.ConversationResponse;
import com.skillswap.chat.dto.ConversationSummaryResponse;
import com.skillswap.chat.dto.CreateConversationRequest;
import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationParticipant;
import com.skillswap.chat.entity.ConversationType;
import com.skillswap.chat.entity.ParticipantStatus;
import com.skillswap.chat.repository.ConversationParticipantRepository;
import com.skillswap.chat.repository.ConversationRepository;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            UserRepository userRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    public ConversationResponse create(
            CreateConversationRequest request,
            String userEmail
    ) {
        request.normalize();

        User currentUser = getUser(userEmail);

        validateParticipants(request);

        Set<Long> participantIds = new HashSet<>(
                request.getParticipantUserIds()
        );

        participantIds.add(currentUser.getId());

        if (request.getType() == ConversationType.DIRECT
                && participantIds.size() != 2) {
            throw new BadRequestException(
                    "A direct conversation must contain exactly two users"
            );
        }

        Conversation conversation = new Conversation(
                request.getType(),
                resolveTitle(request, currentUser)
        );

        conversation.setDescription(request.getDescription());
        conversation.setReferenceType(request.getReferenceType());
        conversation.setReferenceId(request.getReferenceId());

        conversation = conversationRepository.save(conversation);

        for (Long userId : participantIds) {
            User participantUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found: " + userId
                    ));

            ConversationParticipant participant =
                    new ConversationParticipant(
                            conversation,
                            participantUser
                    );

            if (userId.equals(currentUser.getId())) {
                participant.activate();
            }

            participantRepository.save(participant);
        }

        return ConversationResponse.from(conversation);
    }

    /**
     * Creates a direct student-to-student conversation
     * if one does not already exist.
     *
     * If an active direct conversation already exists
     * between the two users, that conversation is returned.
     */
    public ConversationResponse createOrGetDirectConversation(
            Long targetUserId,
            String userEmail
    ) {
        User currentUser = getUser(userEmail);

        if (targetUserId == null) {
            throw new BadRequestException(
                    "Target user ID is required"
            );
        }

        if (currentUser.getId().equals(targetUserId)) {
            throw new BadRequestException(
                    "You cannot create a direct conversation with yourself"
            );
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + targetUserId
                ));

        List<Conversation> existingConversations =
                participantRepository.findActiveConversationBetweenUsers(
                        currentUser.getId(),
                        targetUser.getId(),
                        List.of(
                                ParticipantStatus.INVITED,
                                ParticipantStatus.ACTIVE
                        ),
                        ConversationType.DIRECT
                );

        if (!existingConversations.isEmpty()) {
            return ConversationResponse.from(
                    existingConversations.get(0)
            );
        }

        Conversation conversation = new Conversation(
                ConversationType.DIRECT,
                "Direct Conversation"
        );

        conversation = conversationRepository.save(conversation);

        ConversationParticipant currentParticipant =
                new ConversationParticipant(
                        conversation,
                        currentUser
                );

        currentParticipant.activate();

        participantRepository.save(currentParticipant);

        ConversationParticipant targetParticipant =
                new ConversationParticipant(
                        conversation,
                        targetUser
                );

        targetParticipant.activate();

        participantRepository.save(targetParticipant);

        return ConversationResponse.from(conversation);
    }

    @Transactional(readOnly = true)
    public ConversationResponse getById(
            Long conversationId,
            String userEmail
    ) {
        Conversation conversation = getConversation(conversationId);

        validateParticipant(conversation, userEmail);

        return ConversationResponse.from(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getMyConversations(
            String userEmail
    ) {
        User user = getUser(userEmail);

        List<ConversationParticipant> participants =
                participantRepository.findByUserAndStatusIn(
                        user,
                        List.of(
                                ParticipantStatus.INVITED,
                                ParticipantStatus.ACTIVE
                        )
                );

        List<ConversationSummaryResponse> responses =
                new ArrayList<>();

        for (ConversationParticipant participant : participants) {
            if (participant.getConversation() != null
                    && participant.getConversation().isActive()) {

                responses.add(
                        ConversationSummaryResponse.from(
                                participant.getConversation()
                        )
                );
            }
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public Page<ConversationSummaryResponse> getMyConversationsPage(
            String userEmail,
            int page,
            int size
    ) {
        User user = getUser(userEmail);

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(page, size);

        Page<ConversationParticipant> participantPage =
                participantRepository.findByUserAndStatus(
                        user,
                        ParticipantStatus.ACTIVE,
                        pageable
                );

        return participantPage.map(
                participant -> ConversationSummaryResponse.from(
                        participant.getConversation()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getByType(
            ConversationType type,
            String userEmail
    ) {
        User user = getUser(userEmail);

        List<ConversationParticipant> participants =
                participantRepository.findByUserAndStatusIn(
                        user,
                        List.of(
                                ParticipantStatus.INVITED,
                                ParticipantStatus.ACTIVE
                        )
                );

        List<ConversationSummaryResponse> responses =
                new ArrayList<>();

        for (ConversationParticipant participant : participants) {
            Conversation conversation =
                    participant.getConversation();

            if (conversation != null
                    && conversation.isActive()
                    && conversation.getType() == type) {

                responses.add(
                        ConversationSummaryResponse.from(
                                conversation
                        )
                );
            }
        }

        return responses;
    }

    public void deactivate(
            Long conversationId,
            String userEmail
    ) {
        Conversation conversation =
                getConversation(conversationId);

        validateParticipant(conversation, userEmail);

        conversation.deactivate();

        conversationRepository.save(conversation);
    }

    public void activate(
            Long conversationId,
            String userEmail
    ) {
        Conversation conversation =
                getConversation(conversationId);

        validateParticipant(conversation, userEmail);

        conversation.activate();

        conversationRepository.save(conversation);
    }

    public void leave(
            Long conversationId,
            String userEmail
    ) {
        Conversation conversation =
                getConversation(conversationId);

        User user = getUser(userEmail);

        ConversationParticipant participant =
                participantRepository
                        .findByConversationIdAndUserId(
                                conversationId,
                                user.getId()
                        )
                        .orElseThrow(() -> new BadRequestException(
                                "You are not a participant in this conversation"
                        ));

        if (participant.isRemoved()
                || participant.isBlocked()) {

            throw new BadRequestException(
                    "You cannot leave this conversation"
            );
        }

        participant.leave();

        participantRepository.save(participant);
    }

    public void delete(
            Long conversationId,
            String userEmail
    ) {
        Conversation conversation =
                getConversation(conversationId);

        validateParticipant(conversation, userEmail);

        participantRepository.deleteByConversation(conversation);

        conversationRepository.delete(conversation);
    }

    public Conversation getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation not found: " + conversationId
                ));
    }

    private User getUser(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository.findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email
                ));
    }

    private void validateParticipant(
            Conversation conversation,
            String userEmail
    ) {
        User user = getUser(userEmail);

        boolean participant =
                participantRepository
                        .existsByConversationIdAndUserIdAndStatusIn(
                                conversation.getId(),
                                user.getId(),
                                List.of(
                                        ParticipantStatus.INVITED,
                                        ParticipantStatus.ACTIVE
                                )
                        );

        if (!participant) {
            throw new BadRequestException(
                    "You are not a participant in this conversation"
            );
        }
    }

    private void validateParticipants(
            CreateConversationRequest request
    ) {
        if (request.getParticipantUserIds() == null
                || request.getParticipantUserIds().isEmpty()) {

            throw new BadRequestException(
                    "At least one participant is required"
            );
        }

        Set<Long> uniqueIds = new HashSet<>(
                request.getParticipantUserIds()
        );

        if (uniqueIds.size()
                != request.getParticipantUserIds().size()) {

            throw new BadRequestException(
                    "Duplicate participant user IDs are not allowed"
            );
        }

        for (Long userId : uniqueIds) {
            if (userId == null) {
                throw new BadRequestException(
                        "Participant user ID cannot be null"
                );
            }

            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException(
                        "User not found: " + userId
                );
            }
        }
    }

    private String resolveTitle(
            CreateConversationRequest request,
            User currentUser
    ) {
        if (request.getTitle() != null
                && !request.getTitle().isBlank()) {

            return request.getTitle().trim();
        }

        if (request.getType() == ConversationType.DIRECT) {
            return "Direct Conversation";
        }

        if (request.getType() == ConversationType.GROUP) {
            return "Group Conversation";
        }

        return request.getType().name()
                + " Conversation";
    }
}