package com.skillswap.chat.repository;

import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationParticipant;
import com.skillswap.chat.entity.ConversationType;
import com.skillswap.chat.entity.ParticipantStatus;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantRepository
        extends JpaRepository<ConversationParticipant, Long> {

    List<ConversationParticipant> findByConversation(
            Conversation conversation
    );

    Page<ConversationParticipant> findByConversation(
            Conversation conversation,
            Pageable pageable
    );

    List<ConversationParticipant> findByConversationId(
            Long conversationId
    );

    Page<ConversationParticipant> findByConversationId(
            Long conversationId,
            Pageable pageable
    );

    List<ConversationParticipant> findByUser(
            User user
    );

    Page<ConversationParticipant> findByUser(
            User user,
            Pageable pageable
    );

    List<ConversationParticipant> findByUserAndStatus(
            User user,
            ParticipantStatus status
    );

    Page<ConversationParticipant> findByUserAndStatus(
            User user,
            ParticipantStatus status,
            Pageable pageable
    );

    List<ConversationParticipant> findByUserAndStatusIn(
            User user,
            List<ParticipantStatus> statuses
    );

    Optional<ConversationParticipant> findByConversationIdAndUserId(
            Long conversationId,
            Long userId
    );

    boolean existsByConversationIdAndUserId(
            Long conversationId,
            Long userId
    );

    boolean existsByConversationIdAndUserIdAndStatus(
            Long conversationId,
            Long userId,
            ParticipantStatus status
    );

    boolean existsByConversationIdAndUserIdAndStatusIn(
            Long conversationId,
            Long userId,
            List<ParticipantStatus> statuses
    );

    long countByConversation(
            Conversation conversation
    );

    long countByConversationAndStatus(
            Conversation conversation,
            ParticipantStatus status
    );

    long countByUser(
            User user
    );

    long countByUserAndStatus(
            User user,
            ParticipantStatus status
    );

    void deleteByConversation(
            Conversation conversation
    );

    void deleteByUser(
            User user
    );

    @Query("""
            SELECT cp1.conversation
            FROM ConversationParticipant cp1
            JOIN ConversationParticipant cp2
                ON cp1.conversation.id = cp2.conversation.id
            WHERE cp1.user.id = :userId1
              AND cp2.user.id = :userId2
              AND cp1.status IN :statuses
              AND cp2.status IN :statuses
              AND cp1.conversation.type = :type
              AND cp1.conversation.active = true
            """)
    List<Conversation> findActiveConversationBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("statuses") List<ParticipantStatus> statuses,
            @Param("type") ConversationType type
    );
}