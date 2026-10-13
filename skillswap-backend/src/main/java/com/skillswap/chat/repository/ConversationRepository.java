package com.skillswap.chat.repository;

import com.skillswap.chat.entity.Conversation;
import com.skillswap.chat.entity.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByType(ConversationType type);

    

    Page<Conversation> findByType(
            ConversationType type,
            Pageable pageable
    );

    List<Conversation> findByActiveTrue();

    Page<Conversation> findByActiveTrue(Pageable pageable);

    List<Conversation> findByTypeAndActiveTrue(
            ConversationType type
    );

    Page<Conversation> findByTypeAndActiveTrue(
            ConversationType type,
            Pageable pageable
    );

    List<Conversation> findByReferenceTypeAndReferenceId(
            String referenceType,
            Long referenceId
    );

    Optional<Conversation> findFirstByReferenceTypeAndReferenceId(
            String referenceType,
            Long referenceId
    );

    long countByType(ConversationType type);

    long countByActiveTrue();

    boolean existsByIdAndActiveTrue(Long id);
}