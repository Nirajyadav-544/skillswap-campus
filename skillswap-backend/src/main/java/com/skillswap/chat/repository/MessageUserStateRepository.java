package com.skillswap.chat.repository;

import com.skillswap.chat.entity.Message;
import com.skillswap.chat.entity.MessageUserState;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageUserStateRepository
        extends JpaRepository<MessageUserState, Long> {

    Optional<MessageUserState> findByMessageAndUser(
            Message message,
            User user
    );

    Optional<MessageUserState> findByMessageIdAndUserId(
            Long messageId,
            Long userId
    );

    boolean existsByMessageAndUser(
            Message message,
            User user
    );

    boolean existsByMessageIdAndUserIdAndDeletedForUserTrue(
            Long messageId,
            Long userId
    );

    boolean existsByMessageIdAndUserIdAndStarredTrue(
            Long messageId,
            Long userId
    );

    Page<MessageUserState> findByUserAndStarredTrue(
            User user,
            Pageable pageable
    );

    void deleteByMessage(Message message);

    void deleteByUser(User user);
}