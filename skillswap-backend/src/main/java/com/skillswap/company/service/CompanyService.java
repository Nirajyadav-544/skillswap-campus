package com.skillswap.company.service;

import com.skillswap.company.dto.CompanyResponse;
import com.skillswap.company.dto.CreateCompanyRequest;
import com.skillswap.company.dto.UpdateCompanyRequest;
import com.skillswap.company.entity.Company;
import com.skillswap.company.entity.CompanyStatus;
import com.skillswap.company.repository.CompanyRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository
    ) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public CompanyResponse create(
            Long userId,
            CreateCompanyRequest request
    ) {

        User owner = getUser(userId);

        if (companyRepository.existsByNameIgnoreCase(
                request.getName().trim()
        )) {
            throw new IllegalArgumentException(
                    "A company with this name already exists"
            );
        }

        Company company = new Company(
                request.getName(),
                owner
        );

        applyCreateData(company, request);

        Company saved = companyRepository.save(company);

        return CompanyResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getById(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Company not found"
                        )
                );

        return CompanyResponse.from(company);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getMyCompanies(
            Long userId,
            Pageable pageable
    ) {

        User owner = getUser(userId);

        return companyRepository
                .findByOwner(owner, pageable)
                .map(CompanyResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getActiveCompanies(
            Pageable pageable
    ) {

        return companyRepository
                .findByStatus(
                        CompanyStatus.ACTIVE,
                        pageable
                )
                .map(CompanyResponse::from);
    }

    public CompanyResponse update(
            Long userId,
            Long companyId,
            UpdateCompanyRequest request
    ) {

        User owner = getUser(userId);

        Company company = companyRepository
                .findByIdAndOwner(companyId, owner)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Company not found or you are not the owner"
                        )
                );

        applyUpdateData(company, request);

        return CompanyResponse.from(
                companyRepository.save(company)
        );
    }

    public CompanyResponse activate(
            Long userId,
            Long companyId
    ) {

        Company company = getOwnedCompany(
                userId,
                companyId
        );

        company.setStatus(CompanyStatus.ACTIVE);
        company.setVerified(true);

        return CompanyResponse.from(
                companyRepository.save(company)
        );
    }

    public CompanyResponse suspend(
            Long userId,
            Long companyId
    ) {

        Company company = getOwnedCompany(
                userId,
                companyId
        );

        company.setStatus(CompanyStatus.SUSPENDED);

        return CompanyResponse.from(
                companyRepository.save(company)
        );
    }

    public CompanyResponse archive(
            Long userId,
            Long companyId
    ) {

        Company company = getOwnedCompany(
                userId,
                companyId
        );

        company.setStatus(CompanyStatus.ARCHIVED);

        return CompanyResponse.from(
                companyRepository.save(company)
        );
    }

    public void delete(
            Long userId,
            Long companyId
    ) {

        Company company = getOwnedCompany(
                userId,
                companyId
        );

        companyRepository.delete(company);
    }

    private Company getOwnedCompany(
            Long userId,
            Long companyId
    ) {

        User owner = getUser(userId);

        return companyRepository
                .findByIdAndOwner(companyId, owner)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Company not found or you are not the owner"
                        )
                );
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private void applyCreateData(
            Company company,
            CreateCompanyRequest request
    ) {

        company.setLegalName(request.getLegalName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry());
        company.setCompanySize(request.getCompanySize());
        company.setHeadquarters(request.getHeadquarters());
        company.setLogoUrl(request.getLogoUrl());
        company.setEmail(request.getEmail());
        company.setPhoneNumber(request.getPhoneNumber());
    }

    private void applyUpdateData(
            Company company,
            UpdateCompanyRequest request
    ) {

        if (request.getName() != null) {
            company.setName(request.getName());
        }

        if (request.getLegalName() != null) {
            company.setLegalName(request.getLegalName());
        }

        if (request.getDescription() != null) {
            company.setDescription(request.getDescription());
        }

        if (request.getWebsite() != null) {
            company.setWebsite(request.getWebsite());
        }

        if (request.getIndustry() != null) {
            company.setIndustry(request.getIndustry());
        }

        if (request.getCompanySize() != null) {
            company.setCompanySize(request.getCompanySize());
        }

        if (request.getHeadquarters() != null) {
            company.setHeadquarters(request.getHeadquarters());
        }

        if (request.getLogoUrl() != null) {
            company.setLogoUrl(request.getLogoUrl());
        }

        if (request.getEmail() != null) {
            company.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null) {
            company.setPhoneNumber(request.getPhoneNumber());
        }
    }
}