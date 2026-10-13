package com.skillswap.audit.service;

import com.skillswap.audit.entity.AuditAction;
import com.skillswap.audit.entity.AuditLog;
import com.skillswap.audit.repository.AuditLogRepository;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Creates and saves an audit log.
     */
    public AuditLog log(
            User user,
            AuditAction action,
            String entityType,
            String entityId,
            String description,
            String ipAddress,
            String userAgent
    ) {
        AuditLog auditLog = new AuditLog(
                user,
                action,
                entityType,
                entityId,
                description,
                ipAddress,
                userAgent
        );

        return auditLogRepository.save(auditLog);
    }

    /**
     * Creates a simple audit log without entity/IP information.
     */
    public AuditLog log(
            User user,
            AuditAction action,
            String description
    ) {
        return log(
                user,
                action,
                null,
                null,
                description,
                null,
                null
        );
    }

    /**
     * Returns all audit logs belonging to a user.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getUserLogs(User user) {
        return auditLogRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Returns paginated audit logs belonging to a user.
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getUserLogs(
            User user,
            Pageable pageable
    ) {
        return auditLogRepository
                .findByUserOrderByCreatedAtDesc(
                        user,
                        pageable
                );
    }

    /**
     * Returns audit logs for a particular action.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByAction(
            AuditAction action
    ) {
        return auditLogRepository
                .findByActionOrderByCreatedAtDesc(action);
    }

    /**
     * Returns paginated audit logs for a particular action.
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsByAction(
            AuditAction action,
            Pageable pageable
    ) {
        return auditLogRepository
                .findByActionOrderByCreatedAtDesc(
                        action,
                        pageable
                );
    }

    /**
     * Returns audit logs for a specific entity.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getEntityLogs(
            String entityType,
            String entityId
    ) {
        return auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        entityType,
                        entityId
                );
    }

    /**
     * Counts logs belonging to a user.
     */
    @Transactional(readOnly = true)
    public long countUserLogs(User user) {
        return auditLogRepository.countByUser(user);
    }

    /**
     * Counts logs for a specific action.
     */
    @Transactional(readOnly = true)
    public long countByAction(AuditAction action) {
        return auditLogRepository.countByAction(action);
    }

    /**
     * Returns all audit logs.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    /**
     * Returns paginated audit logs.
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    /**
     * Deletes an audit log by ID.
     */
    public void deleteLog(Long id) {
        auditLogRepository.deleteById(id);
    }
}