package com.skillswap.admin.controller;

import com.skillswap.admin.dto.AdminUserResponse;
import com.skillswap.admin.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService
    ) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        return ResponseEntity.ok(
                adminUserService.getUsers(pageable)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> getUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                adminUserService.getUser(userId)
        );
    }

    @PatchMapping("/{userId}/enable")
    public ResponseEntity<AdminUserResponse> enableUser(
            @RequestParam Long adminId,
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                adminUserService.enableUser(
                        adminId,
                        userId
                )
        );
    }

    @PatchMapping("/{userId}/disable")
    public ResponseEntity<AdminUserResponse> disableUser(
            @RequestParam Long adminId,
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                adminUserService.disableUser(
                        adminId,
                        userId
                )
        );
    }
}