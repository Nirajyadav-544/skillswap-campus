package com.skillswap.admin.controller;

import com.skillswap.admin.dto.AdminDashboardResponse;
import com.skillswap.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {

        return ResponseEntity.ok(
                adminService.getDashboard()
        );
    }

    @GetMapping("/stats/users")
    public ResponseEntity<Long> countUsers() {

        return ResponseEntity.ok(
                adminService.countUsers()
        );
    }

    @GetMapping("/stats/users/active")
    public ResponseEntity<Long> countActiveUsers() {

        return ResponseEntity.ok(
                adminService.countActiveUsers()
        );
    }

    @GetMapping("/stats/users/inactive")
    public ResponseEntity<Long> countInactiveUsers() {

        return ResponseEntity.ok(
                adminService.countInactiveUsers()
        );
    }

    @GetMapping("/stats/companies")
    public ResponseEntity<Long> countCompanies() {

        return ResponseEntity.ok(
                adminService.countCompanies()
        );
    }

    @GetMapping("/stats/companies/active")
    public ResponseEntity<Long> countActiveCompanies() {

        return ResponseEntity.ok(
                adminService.countActiveCompanies()
        );
    }

    @GetMapping("/stats/companies/pending")
    public ResponseEntity<Long> countPendingCompanies() {

        return ResponseEntity.ok(
                adminService.countPendingCompanies()
        );
    }
}