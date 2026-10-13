package com.skillswap.audit.repository;

import com.skillswap.audit.entity.AuditAction;
import com.skillswap.audit.entity.AuditLog;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByCreatedAtDesc(User user);

    Page<AuditLog> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );

    List<AuditLog> findByActionOrderByCreatedAtDesc(
            AuditAction action
    );

    Page<AuditLog> findByActionOrderByCreatedAtDesc(
            AuditAction action,
            Pageable pageable
    );

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType,
            String entityId
    );

    long countByUser(User user);

    long countByAction(AuditAction action);
}