package com.skillswap.chat.repository;

import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.Message;
import com.skillswap.chat.entity.MessageStatus;
import com.skillswap.chat.entity.MessageType;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(
            Conversation conversation
    );

    Page<Message> findByConversationOrderByCreatedAtDesc(
            Conversation conversation,
            Pageable pageable
    );

    List<Message> findByConversationIdOrderByCreatedAtAsc(
            Long conversationId
    );

    Page<Message> findByConversationIdOrderByCreatedAtDesc(
            Long conversationId,
            Pageable pageable
    );

    List<Message> findByConversationAndStatus(
            Conversation conversation,
            MessageStatus status
    );

    Page<Message> findByConversationAndStatus(
            Conversation conversation,
            MessageStatus status,
            Pageable pageable
    );

    List<Message> findByConversationAndType(
            Conversation conversation,
            MessageType type
    );

    Page<Message> findByConversationAndType(
            Conversation conversation,
            MessageType type,
            Pageable pageable
    );

    List<Message> findBySenderUser(
            User senderUser
    );

    Page<Message> findBySenderUser(
            User senderUser,
            Pageable pageable
    );

    long countByConversation(
            Conversation conversation
    );

    long countByConversationAndStatus(
            Conversation conversation,
            MessageStatus status
    );

    long countBySenderUser(
            User senderUser
    );

    boolean existsByIdAndConversation(
            Long id,
            Conversation conversation
    );

    boolean existsByIdAndSenderUser(
            Long id,
            User senderUser
    );

    List<Message> findByConversationAndCreatedAtAfterOrderByCreatedAtAsc(
            Conversation conversation,
            LocalDateTime createdAt
    );

    Page<Message> findByConversationAndCreatedAtAfterOrderByCreatedAtDesc(
            Conversation conversation,
            LocalDateTime createdAt,
            Pageable pageable
    );

    void deleteByConversation(
            Conversation conversation
    );

    void deleteBySenderUser(
            User senderUser
    );
}