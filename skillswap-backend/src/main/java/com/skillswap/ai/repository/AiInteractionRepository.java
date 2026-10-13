package com.skillswap.ai.repository;

import com.skillswap.ai.entity.AiInteraction;
import com.skillswap.ai.entity.AiInteractionStatus;
import com.skillswap.ai.entity.AiInteractionType;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiInteractionRepository
        extends JpaRepository<AiInteraction, Long> {

    List<AiInteraction> findByUser(User user);

    Page<AiInteraction> findByUser(
            User user,
            Pageable pageable
    );

    List<AiInteraction> findByUserAndInteractionType(
            User user,
            AiInteractionType interactionType
    );

    Page<AiInteraction> findByUserAndInteractionType(
            User user,
            AiInteractionType interactionType,
            Pageable pageable
    );

    List<AiInteraction> findByUserAndStatus(
            User user,
            AiInteractionStatus status
    );

    Page<AiInteraction> findByUserAndStatus(
            User user,
            AiInteractionStatus status,
            Pageable pageable
    );

    Optional<AiInteraction> findByIdAndUser(
            Long id,
            User user
    );

    long countByUser(User user);

    long countByUserAndStatus(
            User user,
            AiInteractionStatus status
    );

    long countByUserAndInteractionType(
            User user,
            AiInteractionType interactionType
    );

    void deleteByUser(User user);
}