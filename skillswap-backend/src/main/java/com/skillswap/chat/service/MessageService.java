package com.skillswap.chat.service;

import com.skillswap.chat.dto.MessageResponse;
import com.skillswap.chat.dto.MessageSummaryResponse;
import com.skillswap.chat.dto.SendMessageRequest;
import com.skillswap.chat.dto.UpdateMessageRequest;
import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationParticipant;
import com.skillswap.chat.entity.Message;
import com.skillswap.chat.entity.MessageStatus;
import com.skillswap.chat.entity.MessageType;
import com.skillswap.chat.entity.MessageUserState;
import com.skillswap.chat.entity.ParticipantStatus;
import com.skillswap.chat.repository.ConversationParticipantRepository;
import com.skillswap.chat.repository.ConversationRepository;
import com.skillswap.chat.repository.MessageRepository;
import com.skillswap.chat.repository.MessageUserStateRepository;
import com.skillswap.common.exception.BadRequestException;
import com.skillswap.common.exception.ResourceNotFoundException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageUserStateRepository messageUserStateRepository;
    private final UserRepository userRepository;

    public MessageService(
            MessageRepository messageRepository,
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            MessageUserStateRepository messageUserStateRepository,
            UserRepository userRepository
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageUserStateRepository = messageUserStateRepository;
        this.userRepository = userRepository;
    }

    public MessageResponse sendMessage(
            Long conversationId,
            SendMessageRequest request,
            String userEmail
    ) {
        request.normalize();

        if (request.isEmpty()) {
            throw new BadRequestException(
                    "Message content or attachment is required"
            );
        }

        Conversation conversation = getConversation(conversationId);
        User sender = getUser(userEmail);

        validateActiveParticipant(
                conversationId,
                sender.getId()
        );

        validateMessageType(request);

        if (request.getReplyToMessageId() != null) {
            validateReplyMessage(
                    conversationId,
                    request.getReplyToMessageId()
            );
        }

        Message message = new Message(
                conversation,
                sender,
                request.getType(),
                request.getContent()
        );

        message.setAttachmentUrl(request.getAttachmentUrl());
        message.setAttachmentName(request.getAttachmentName());
        message.setAttachmentType(request.getAttachmentType());
        message.setReplyToMessageId(request.getReplyToMessageId());

        message = messageRepository.save(message);

        conversation.updateLastMessageTime();
        conversationRepository.save(conversation);

        return MessageResponse.from(message);
    }

    @Transactional(readOnly = true)
    public MessageResponse getById(
            Long messageId,
            String userEmail
    ) {
        Message message = getMessage(messageId);

        validateActiveParticipant(
                message.getConversation().getId(),
                getUser(userEmail).getId()
        );

        return MessageResponse.from(message);
    }

    @Transactional(readOnly = true)
    public List<MessageSummaryResponse> getMessages(
            Long conversationId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        validateActiveParticipant(
                conversationId,
                user.getId()
        );

        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(MessageSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<MessageSummaryResponse> getMessagesPage(
            Long conversationId,
            String userEmail,
            int page,
            int size
    ) {
        User user = getUser(userEmail);

        validateActiveParticipant(
                conversationId,
                user.getId()
        );

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(page, size);

        return messageRepository
                .findByConversationIdOrderByCreatedAtDesc(
                        conversationId,
                        pageable
                )
                .map(MessageSummaryResponse::from);
    }

    public MessageResponse updateMessage(
            Long messageId,
            UpdateMessageRequest request,
            String userEmail
    ) {
        request.normalize();

        if (request.getContent() == null
                || request.getContent().isBlank()) {

            throw new BadRequestException(
                    "Message content is required"
            );
        }

        Message message = getMessage(messageId);
        User user = getUser(userEmail);

        validateActiveParticipant(
                message.getConversation().getId(),
                user.getId()
        );

        if (!message.getSenderUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "Only the sender can edit this message"
            );
        }

        if (message.isDeleted()) {
            throw new BadRequestException(
                    "Deleted messages cannot be edited"
            );
        }

        message.markEdited(request.getContent());

        return MessageResponse.from(
                messageRepository.save(message)
        );
    }

    /*
     * Delete for everyone
     *
     * Existing behavior preserved.
     */
    public void deleteMessage(
            Long messageId,
            String userEmail
    ) {
        Message message = getMessage(messageId);
        User user = getUser(userEmail);

        validateActiveParticipant(
                message.getConversation().getId(),
                user.getId()
        );

        if (!message.getSenderUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "Only the sender can delete this message"
            );
        }

        if (message.isDeleted()) {
            return;
        }

        message.markDeleted();
        messageRepository.save(message);
    }

    /*
     * Delete for me
     *
     * This does NOT modify the actual Message.
     * Therefore the other participant can still see it.
     */
    public void deleteForMe(
            Long messageId,
            String userEmail
    ) {
        Message message = getMessage(messageId);
        User user = getUser(userEmail);

        validateActiveParticipant(
                message.getConversation().getId(),
                user.getId()
        );

        MessageUserState state =
                messageUserStateRepository
                        .findByMessageAndUser(message, user)
                        .orElseGet(() ->
                                new MessageUserState(message, user)
                        );

        state.markDeletedForUser();

        messageUserStateRepository.save(state);
    }

    /*
     * Check whether this particular user deleted
     * the message for themselves.
     */
    @Transactional(readOnly = true)
    public boolean isDeletedForMe(
            Long messageId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        return messageUserStateRepository
                .existsByMessageIdAndUserIdAndDeletedForUserTrue(
                        messageId,
                        user.getId()
                );
    }

    /*
     * Star / Unstar message
     *
     * Returns true when message is now starred.
     * Returns false when message is now unstarred.
     */
    public boolean toggleStar(
            Long messageId,
            String userEmail
    ) {
        Message message = getMessage(messageId);
        User user = getUser(userEmail);

        validateActiveParticipant(
                message.getConversation().getId(),
                user.getId()
        );

        MessageUserState state =
                messageUserStateRepository
                        .findByMessageAndUser(message, user)
                        .orElseGet(() ->
                                new MessageUserState(message, user)
                        );

        if (state.isStarred()) {
            state.unstar();
        } else {
            state.star();
        }

        messageUserStateRepository.save(state);

        return state.isStarred();
    }

    @Transactional(readOnly = true)
    public boolean isStarred(
            Long messageId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        return messageUserStateRepository
                .existsByMessageIdAndUserIdAndStarredTrue(
                        messageId,
                        user.getId()
                );
    }

    /*
     * Get this user's starred messages.
     */
    @Transactional(readOnly = true)
    public List<MessageSummaryResponse> getStarredMessages(
            String userEmail
    ) {
        User user = getUser(userEmail);

        return messageUserStateRepository
                .findByUserAndStarredTrue(
                        user,
                        PageRequest.of(0, 100)
                )
                .getContent()
                .stream()
                .filter(state ->
                        !state.isDeletedForUser()
                )
                .map(state ->
                        MessageSummaryResponse.from(
                                state.getMessage()
                        )
                )
                .toList();
    }

    public MessageResponse markDelivered(
            Long messageId,
            String userEmail
    ) {
        Message message = getMessage(messageId);
        User user = getUser(userEmail);

        validateActiveParticipant(
                message.getConversation().getId(),
                user.getId()
        );

        if (!message.isDeleted()) {
            message.markDelivered();
            messageRepository.save(message);
        }

        return MessageResponse.from(message);
    }

    public MessageResponse markRead(
            Long messageId,
            String userEmail
    ) {
        Message message = getMessage(messageId);
        User user = getUser(userEmail);

        validateActiveParticipant(
                message.getConversation().getId(),
                user.getId()
        );

        if (!message.isDeleted()) {
            message.markRead();
            messageRepository.save(message);
        }

        markParticipantRead(
                message.getConversation().getId(),
                user.getId()
        );

        return MessageResponse.from(message);
    }

   public List<MessageSummaryResponse> getStarredMessages(
        Long conversationId,
        String userEmail
) {
    return getStarredMessages(userEmail)
            .stream()
            .filter(message ->
                    conversationId.equals(message.getConversationId())
            )
            .toList();
}



    @Transactional(readOnly = true)
    public long countMessages(
            Long conversationId,
            String userEmail
    ) {
        User user = getUser(userEmail);

        validateActiveParticipant(
                conversationId,
                user.getId()
        );

        Conversation conversation =
                getConversation(conversationId);

        return messageRepository.countByConversation(conversation);
    }

    @Transactional(readOnly = true)
    public List<MessageSummaryResponse> getMessagesByStatus(
            Long conversationId,
            MessageStatus status,
            String userEmail
    ) {
        User user = getUser(userEmail);

        validateActiveParticipant(
                conversationId,
                user.getId()
        );

        Conversation conversation =
                getConversation(conversationId);

        return messageRepository
                .findByConversationAndStatus(
                        conversation,
                        status
                )
                .stream()
                .map(MessageSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageSummaryResponse> getMessagesByType(
            Long conversationId,
            MessageType type,
            String userEmail
    ) {
        User user = getUser(userEmail);

        validateActiveParticipant(
                conversationId,
                user.getId()
        );

        Conversation conversation =
                getConversation(conversationId);

        return messageRepository
                .findByConversationAndType(
                        conversation,
                        type
                )
                .stream()
                .map(MessageSummaryResponse::from)
                .toList();
    }

    private Message getMessage(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Message not found: " + messageId
                        )
                );
    }

    private Conversation getConversation(
            Long conversationId
    ) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conversation not found: " + conversationId
                        )
                );
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + email
                        )
                );
    }

    private void validateActiveParticipant(
            Long conversationId,
            Long userId
    ) {
        boolean active =
                participantRepository
                        .existsByConversationIdAndUserIdAndStatus(
                                conversationId,
                                userId,
                                ParticipantStatus.ACTIVE
                        );

        if (!active) {
            throw new BadRequestException(
                    "You are not an active participant in this conversation"
            );
        }
    }

    private void validateReplyMessage(
            Long conversationId,
            Long messageId
    ) {
        boolean exists =
                messageRepository.existsByIdAndConversation(
                        messageId,
                        getConversation(conversationId)
                );

        if (!exists) {
            throw new ResourceNotFoundException(
                    "Reply message not found in this conversation: "
                            + messageId
            );
        }
    }

    private void validateMessageType(
            SendMessageRequest request
    ) {
        if (request.getType() == null) {
            request.setType(MessageType.TEXT);
        }

        if (request.getType() == MessageType.TEXT
                && !request.hasContent()) {

            throw new BadRequestException(
                    "Text messages require content"
            );
        }

        if (request.getType() != MessageType.TEXT
                && !request.hasContent()
                && !request.hasAttachment()) {

            throw new BadRequestException(
                    "This message type requires content or an attachment"
            );
        }
    }

    private void markParticipantRead(
            Long conversationId,
            Long userId
    ) {
        participantRepository
                .findByConversationIdAndUserId(
                        conversationId,
                        userId
                )
                .ifPresent(
                        ConversationParticipant::markRead
                );
    }
}