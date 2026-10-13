package com.skillswap.company.repository;

import com.skillswap.company.entity.Company;
import com.skillswap.company.entity.CompanyMember;
import com.skillswap.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyMemberRepository
        extends JpaRepository<CompanyMember, Long> {

    List<CompanyMember> findByCompany(
            Company company
    );

    List<CompanyMember> findByCompanyAndActiveTrue(
            Company company
    );

    List<CompanyMember> findByUser(
            User user
    );

    Optional<CompanyMember> findByIdAndCompany(
            Long id,
            Company company
    );

    Optional<CompanyMember> findByCompanyAndUser(
            Company company,
            User user
    );

    boolean existsByCompanyAndUser(
            Company company,
            User user
    );

    boolean existsByCompanyAndUserAndActiveTrue(
            Company company,
            User user
    );

    long countByCompany(
            Company company
    );

    long countByCompanyAndActiveTrue(
            Company company
    );

    void deleteByCompany(
            Company company
    );
}