package com.skillswap.admin.repository;

import com.skillswap.admin.entity.AdminAction;
import com.skillswap.admin.entity.AdminAuditLog;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long> {

    List<AdminAuditLog> findByAdmin(User admin);

    Page<AdminAuditLog> findByAdmin(
            User admin,
            Pageable pageable
    );

    List<AdminAuditLog> findByAction(AdminAction action);

    Page<AdminAuditLog> findByAction(
            AdminAction action,
            Pageable pageable
    );

    List<AdminAuditLog> findByTargetTypeAndTargetId(
            String targetType,
            Long targetId
    );

    Page<AdminAuditLog> findByTargetTypeAndTargetId(
            String targetType,
            Long targetId,
            Pageable pageable
    );

    long countByAdmin(User admin);

    long countByAction(AdminAction action);
}