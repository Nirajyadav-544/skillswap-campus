package com.skillswap.admin.service;

import com.skillswap.admin.entity.AdminAction;
import com.skillswap.admin.entity.AdminAuditLog;
import com.skillswap.admin.repository.AdminAuditLogRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminAuditService {

    private final AdminAuditLogRepository adminAuditLogRepository;
    private final UserRepository userRepository;

    public AdminAuditService(
            AdminAuditLogRepository adminAuditLogRepository,
            UserRepository userRepository
    ) {
        this.adminAuditLogRepository = adminAuditLogRepository;
        this.userRepository = userRepository;
    }

    public AdminAuditLog log(
            Long adminId,
            AdminAction action,
            String targetType,
            Long targetId,
            String description
    ) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Admin user not found")
                );

        AdminAuditLog auditLog = new AdminAuditLog(
                admin,
                action,
                targetType,
                targetId,
                description
        );

        return adminAuditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditLog> getLogs(Pageable pageable) {
        return adminAuditLogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditLog> getLogsByAdmin(
            Long adminId,
            Pageable pageable
    ) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Admin user not found")
                );

        return adminAuditLogRepository.findByAdmin(
                admin,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditLog> getLogsByAction(
            AdminAction action,
            Pageable pageable
    ) {
        return adminAuditLogRepository.findByAction(
                action,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditLog> getLogsByTarget(
            String targetType,
            Long targetId,
            Pageable pageable
    ) {
        return adminAuditLogRepository.findByTargetTypeAndTargetId(
                targetType,
                targetId,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public long countLogs() {
        return adminAuditLogRepository.count();
    }
}