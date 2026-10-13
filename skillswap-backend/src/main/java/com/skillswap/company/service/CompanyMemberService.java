package com.skillswap.company.service;

import com.skillswap.company.dto.AddCompanyMemberRequest;
import com.skillswap.company.dto.CompanyMemberResponse;
import com.skillswap.company.entity.Company;
import com.skillswap.company.entity.CompanyMember;
import com.skillswap.company.repository.CompanyMemberRepository;
import com.skillswap.company.repository.CompanyRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyMemberService {

    private final CompanyMemberRepository companyMemberRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyMemberService(
            CompanyMemberRepository companyMemberRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository
    ) {
        this.companyMemberRepository = companyMemberRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public CompanyMemberResponse addMember(
            Long ownerId,
            Long companyId,
            AddCompanyMemberRequest request
    ) {

        Company company = getOwnedCompany(
                ownerId,
                companyId
        );

        User user = userRepository.findById(
                request.getUserId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "User not found"
                )
        );

        if (companyMemberRepository.existsByCompanyAndUser(
                company,
                user
        )) {
            throw new IllegalArgumentException(
                    "User is already a member of this company"
            );
        }

        String role = request.getRole();

        if (role == null || role.trim().isEmpty()) {
            role = "MEMBER";
        }

        CompanyMember member = new CompanyMember(
                company,
                user,
                role
        );

        return CompanyMemberResponse.from(
                companyMemberRepository.save(member)
        );
    }

    @Transactional(readOnly = true)
    public List<CompanyMemberResponse> getMembers(
            Long companyId
    ) {

        Company company = companyRepository.findById(
                companyId
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Company not found"
                )
        );

        return companyMemberRepository
                .findByCompanyAndActiveTrue(company)
                .stream()
                .map(CompanyMemberResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyMemberResponse getMember(
            Long companyId,
            Long memberId
    ) {

        Company company = companyRepository.findById(
                companyId
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Company not found"
                )
        );

        CompanyMember member =
                companyMemberRepository
                        .findByIdAndCompany(
                                memberId,
                                company
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Company member not found"
                                )
                        );

        return CompanyMemberResponse.from(member);
    }

    public CompanyMemberResponse deactivateMember(
            Long ownerId,
            Long companyId,
            Long memberId
    ) {

        Company company = getOwnedCompany(
                ownerId,
                companyId
        );

        CompanyMember member =
                companyMemberRepository
                        .findByIdAndCompany(
                                memberId,
                                company
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Company member not found"
                                )
                        );

        member.setActive(false);

        return CompanyMemberResponse.from(
                companyMemberRepository.save(member)
        );
    }

    public CompanyMemberResponse activateMember(
            Long ownerId,
            Long companyId,
            Long memberId
    ) {

        Company company = getOwnedCompany(
                ownerId,
                companyId
        );

        CompanyMember member =
                companyMemberRepository
                        .findByIdAndCompany(
                                memberId,
                                company
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Company member not found"
                                )
                        );

        member.setActive(true);

        return CompanyMemberResponse.from(
                companyMemberRepository.save(member)
        );
    }

    public void removeMember(
            Long ownerId,
            Long companyId,
            Long memberId
    ) {

        Company company = getOwnedCompany(
                ownerId,
                companyId
        );

        CompanyMember member =
                companyMemberRepository
                        .findByIdAndCompany(
                                memberId,
                                company
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Company member not found"
                                )
                        );

        companyMemberRepository.delete(member);
    }

    private Company getOwnedCompany(
            Long ownerId,
            Long companyId
    ) {

        User owner = userRepository.findById(
                ownerId
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "User not found"
                )
        );

        return companyRepository
                .findByIdAndOwner(
                        companyId,
                        owner
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Company not found or you are not the owner"
                        )
                );
    }
}