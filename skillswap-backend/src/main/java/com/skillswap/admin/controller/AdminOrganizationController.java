package com.skillswap.admin.controller;

import com.skillswap.admin.dto.AdminOrganizationResponse;
import com.skillswap.admin.service.AdminOrganizationService;
import com.skillswap.company.entity.CompanyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/organizations")
public class AdminOrganizationController {

    private final AdminOrganizationService adminOrganizationService;

    public AdminOrganizationController(
            AdminOrganizationService adminOrganizationService
    ) {
        this.adminOrganizationService = adminOrganizationService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminOrganizationResponse>> getOrganizations(
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
                adminOrganizationService.getOrganizations(pageable)
        );
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<AdminOrganizationResponse> getOrganization(
            @PathVariable Long companyId
    ) {
        return ResponseEntity.ok(
                adminOrganizationService.getOrganization(companyId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<AdminOrganizationResponse>>
    getOrganizationsByStatus(
            @PathVariable CompanyStatus status,
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
                adminOrganizationService.getOrganizationsByStatus(
                        status,
                        pageable
                )
        );
    }

    @PatchMapping("/{companyId}/activate")
    public ResponseEntity<AdminOrganizationResponse>
    activateOrganization(
            @RequestParam Long adminId,
            @PathVariable Long companyId
    ) {
        return ResponseEntity.ok(
                adminOrganizationService.activateOrganization(
                        adminId,
                        companyId
                )
        );
    }

    @PatchMapping("/{companyId}/deactivate")
    public ResponseEntity<AdminOrganizationResponse>
    deactivateOrganization(
            @RequestParam Long adminId,
            @PathVariable Long companyId
    ) {
        return ResponseEntity.ok(
                adminOrganizationService.deactivateOrganization(
                        adminId,
                        companyId
                )
        );
    }
}