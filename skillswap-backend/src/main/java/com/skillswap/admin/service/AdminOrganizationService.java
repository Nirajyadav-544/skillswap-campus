package com.skillswap.admin.service;

import com.skillswap.admin.dto.AdminOrganizationResponse;
import com.skillswap.admin.entity.AdminAction;
import com.skillswap.company.entity.Company;
import com.skillswap.company.entity.CompanyStatus;
import com.skillswap.company.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminOrganizationService {

    private final CompanyRepository companyRepository;
    private final AdminAuditService adminAuditService;

    public AdminOrganizationService(
            CompanyRepository companyRepository,
            AdminAuditService adminAuditService
    ) {
        this.companyRepository = companyRepository;
        this.adminAuditService = adminAuditService;
    }

    @Transactional(readOnly = true)
    public Page<AdminOrganizationResponse> getOrganizations(
            Pageable pageable
    ) {
        return companyRepository.findAll(pageable)
                .map(AdminOrganizationResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminOrganizationResponse getOrganization(Long companyId) {

        Company company = getCompany(companyId);

        return AdminOrganizationResponse.from(company);
    }

    @Transactional(readOnly = true)
    public Page<AdminOrganizationResponse> getOrganizationsByStatus(
            CompanyStatus status,
            Pageable pageable
    ) {
        return companyRepository.findByStatus(status, pageable)
                .map(AdminOrganizationResponse::from);
    }

    public AdminOrganizationResponse activateOrganization(
            Long adminId,
            Long companyId
    ) {
        Company company = getCompany(companyId);

        company.setStatus(CompanyStatus.ACTIVE);
        company.setVerified(true);

        Company saved = companyRepository.save(company);

        adminAuditService.log(
                adminId,
                AdminAction.ORGANIZATION_ACTIVATED,
                "COMPANY",
                companyId,
                "Company organization activated"
        );

        return AdminOrganizationResponse.from(saved);
    }

    public AdminOrganizationResponse deactivateOrganization(
            Long adminId,
            Long companyId
    ) {
        Company company = getCompany(companyId);

        company.setStatus(CompanyStatus.SUSPENDED);

        Company saved = companyRepository.save(company);

        adminAuditService.log(
                adminId,
                AdminAction.ORGANIZATION_DEACTIVATED,
                "COMPANY",
                companyId,
                "Company organization deactivated"
        );

        return AdminOrganizationResponse.from(saved);
    }

    private Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Company not found")
                );
    }
}