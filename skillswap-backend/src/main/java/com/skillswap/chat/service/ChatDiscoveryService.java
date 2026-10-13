package com.skillswap.chat.service;

import com.skillswap.chat.dto.ChatPageResponse;
import com.skillswap.chat.dto.ChatSearchRequest;
import com.skillswap.chat.dto.ConversationSummaryResponse;
import com.skillswap.chat.dto.MessageSummaryResponse;
import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.Message;
import com.skillswap.chat.entity.ParticipantStatus;
import com.skillswap.chat.repository.ConversationParticipantRepository;
import com.skillswap.chat.repository.ConversationRepository;
import com.skillswap.chat.repository.MessageRepository;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChatDiscoveryService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatDiscoveryService(
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            MessageRepository messageRepository,
            UserRepository userRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public ChatPageResponse<ConversationSummaryResponse> searchConversations(
            ChatSearchRequest request,
            String userEmail
    ) {
        request.normalize();

        User user = getUser(userEmail);

        List<Conversation> conversations;

        if (request.getConversationId() != null) {

            Conversation conversation =
                    conversationRepository.findById(
                            request.getConversationId()
                    ).orElse(null);

            if (conversation == null) {
                conversations = new ArrayList<>();
            } else {
                conversations = new ArrayList<>();
                conversations.add(conversation);
            }

        } else if (request.getConversationType() != null) {

            conversations =
                    conversationRepository.findByTypeAndActiveTrue(
                            request.getConversationType()
                    );

        } else {

            conversations =
                    conversationRepository.findByActiveTrue();
        }

        List<ConversationSummaryResponse> filtered =
                conversations.stream()
                        .filter(conversation ->
                                isParticipant(
                                        conversation.getId(),
                                        user.getId()
                                )
                        )
                        .filter(conversation ->
                                matchesConversationKeyword(
                                        conversation,
                                        request.getKeyword()
                                )
                        )
                        .map(ConversationSummaryResponse::from)
                        .toList();

        return createPage(
                filtered,
                request.getPage(),
                request.getSize()
        );
    }

    public ChatPageResponse<MessageSummaryResponse> searchMessages(
            ChatSearchRequest request,
            String userEmail
    ) {
        request.normalize();

        User user = getUser(userEmail);

        List<Message> messages;

        if (request.getConversationId() != null) {

            Long conversationId =
                    request.getConversationId();

            validateParticipant(
                    conversationId,
                    user.getId()
            );

            Conversation conversation =
                    conversationRepository.findById(
                            conversationId
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Conversation not found: "
                                            + conversationId
                            )
                    );

            if (request.getMessageStatus() != null) {

                messages =
                        messageRepository.findByConversationAndStatus(
                                conversation,
                                request.getMessageStatus()
                        );

            } else if (request.getMessageType() != null) {

                messages =
                        messageRepository.findByConversationAndType(
                                conversation,
                                request.getMessageType()
                        );

            } else {

                messages =
                        messageRepository
                                .findByConversationOrderByCreatedAtAsc(
                                        conversation
                                );
            }

        } else {

            messages =
                    messageRepository.findBySenderUser(user);
        }

        List<MessageSummaryResponse> filtered =
                messages.stream()
                        .filter(message ->
                                matchesMessageKeyword(
                                        message,
                                        request.getKeyword()
                                )
                        )
                        .map(MessageSummaryResponse::from)
                        .toList();

        return createPage(
                filtered,
                request.getPage(),
                request.getSize()
        );
    }

    public List<ConversationSummaryResponse> getMyConversations(
            String userEmail
    ) {
        User user = getUser(userEmail);

        return participantRepository
                .findByUserAndStatus(
                        user,
                        ParticipantStatus.ACTIVE
                )
                .stream()
                .map(participant ->
                        participant.getConversation()
                )
                .filter(Conversation::isActive)
                .distinct()
                .map(ConversationSummaryResponse::from)
                .toList();
    }

    public List<MessageSummaryResponse> searchConversationMessages(
            Long conversationId,
            String keyword,
            String userEmail
    ) {
        User user = getUser(userEmail);

        validateParticipant(
                conversationId,
                user.getId()
        );

        Conversation conversation =
                conversationRepository.findById(
                        conversationId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conversation not found: "
                                        + conversationId
                        )
                );

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim().toLowerCase();

        return messageRepository
                .findByConversationOrderByCreatedAtAsc(
                        conversation
                )
                .stream()
                .filter(message ->
                        normalizedKeyword.isBlank()
                                || containsIgnoreCase(
                                message.getContent(),
                                normalizedKeyword
                        )
                )
                .map(MessageSummaryResponse::from)
                .toList();
    }

    private boolean matchesConversationKeyword(
            Conversation conversation,
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String value =
                keyword.trim().toLowerCase();

        return containsIgnoreCase(
                conversation.getTitle(),
                value
        ) || containsIgnoreCase(
                conversation.getDescription(),
                value
        ) || containsIgnoreCase(
                conversation.getReferenceType(),
                value
        );
    }

    private boolean matchesMessageKeyword(
            Message message,
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        return containsIgnoreCase(
                message.getContent(),
                keyword.trim().toLowerCase()
        );
    }

    private boolean containsIgnoreCase(
            String source,
            String keyword
    ) {
        if (source == null || keyword == null) {
            return false;
        }

        return source
                .toLowerCase()
                .contains(keyword.toLowerCase());
    }

    private boolean isParticipant(
            Long conversationId,
            Long userId
    ) {
        return participantRepository
                .existsByConversationIdAndUserId(
                        conversationId,
                        userId
                );
    }

    private void validateParticipant(
            Long conversationId,
            Long userId
    ) {
        if (!isParticipant(conversationId, userId)) {
            throw new BadRequestException(
                    "You are not a participant in this conversation"
            );
        }
    }

    private User getUser(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + email
                        )
                );
    }

    private <T> ChatPageResponse<T> createPage(
            List<T> content,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);

        int safeSize = Math.min(
                Math.max(size, 1),
                100
        );

        int totalElements = content.size();

        int totalPages =
                totalElements == 0
                        ? 0
                        : (int) Math.ceil(
                                (double) totalElements / safeSize
                        );

        int fromIndex =
                safePage * safeSize;

        int toIndex =
                Math.min(
                        fromIndex + safeSize,
                        totalElements
                );

        List<T> pageContent;

        if (fromIndex >= totalElements) {
            pageContent = List.of();
        } else {
            pageContent =
                    content.subList(
                            fromIndex,
                            toIndex
                    );
        }

        ChatPageResponse<T> response =
                new ChatPageResponse<>();

        response.setContent(pageContent);
        response.setPage(safePage);
        response.setSize(safeSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);

        return response;
    }
}