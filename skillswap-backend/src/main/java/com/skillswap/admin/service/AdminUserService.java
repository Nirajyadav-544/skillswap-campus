package com.skillswap.admin.service;

import com.skillswap.admin.dto.AdminUserResponse;
import com.skillswap.admin.entity.AdminAction;
import com.skillswap.admin.mapper.AdminMapper;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final AdminMapper adminMapper;
    private final AdminAuditService adminAuditService;

    public AdminUserService(
            UserRepository userRepository,
            AdminMapper adminMapper,
            AdminAuditService adminAuditService
    ) {
        this.userRepository = userRepository;
        this.adminMapper = adminMapper;
        this.adminAuditService = adminAuditService;
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(adminMapper::toUserResponse);
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUser(Long userId) {
        User user = getUserEntity(userId);

        return adminMapper.toUserResponse(user);
    }

    public AdminUserResponse enableUser(
            Long adminId,
            Long userId
    ) {
        User user = getUserEntity(userId);

        user.setEnabled(true);

        User saved = userRepository.save(user);

        adminAuditService.log(
                adminId,
                AdminAction.USER_ACTIVATED,
                "USER",
                userId,
                "User account activated"
        );

        return adminMapper.toUserResponse(saved);
    }

    public AdminUserResponse disableUser(
            Long adminId,
            Long userId
    ) {
        User user = getUserEntity(userId);

        user.setEnabled(false);

        User saved = userRepository.save(user);

        adminAuditService.log(
                adminId,
                AdminAction.USER_DEACTIVATED,
                "USER",
                userId,
                "User account deactivated"
        );

        return adminMapper.toUserResponse(saved);
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );
    }
}