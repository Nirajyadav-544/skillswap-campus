package com.skillswap.admin.service;

import com.skillswap.admin.dto.AdminDashboardResponse;
import com.skillswap.company.entity.CompanyStatus;
import com.skillswap.company.repository.CompanyRepository;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final AdminAuditService adminAuditService;

    public AdminService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            AdminAuditService adminAuditService
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.adminAuditService = adminAuditService;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {

        AdminDashboardResponse response = new AdminDashboardResponse();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabled(true);
        long inactiveUsers = userRepository.countByEnabled(false);

        long totalCompanies = companyRepository.count();
        long activeCompanies =
                companyRepository.countByStatus(CompanyStatus.ACTIVE);
        long pendingCompanies =
                companyRepository.countByStatus(CompanyStatus.PENDING);

        response.setTotalUsers(totalUsers);
        response.setActiveUsers(activeUsers);
        response.setInactiveUsers(inactiveUsers);

        response.setTotalCompanies(totalCompanies);
        response.setActiveCompanies(activeCompanies);
        response.setPendingCompanies(pendingCompanies);

        response.setTotalAuditLogs(adminAuditService.countLogs());

        return response;
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByEnabled(true);
    }

    @Transactional(readOnly = true)
    public long countInactiveUsers() {
        return userRepository.countByEnabled(false);
    }

    @Transactional(readOnly = true)
    public long countCompanies() {
        return companyRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveCompanies() {
        return companyRepository.countByStatus(CompanyStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public long countPendingCompanies() {
        return companyRepository.countByStatus(CompanyStatus.PENDING);
    }
}