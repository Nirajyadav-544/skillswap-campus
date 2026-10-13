package com.skillswap.company.repository;

import com.skillswap.company.entity.Company;
import com.skillswap.company.entity.CompanyStatus;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByOwner(User owner);

    Page<Company> findByOwner(
            User owner,
            Pageable pageable
    );

    List<Company> findByStatus(CompanyStatus status);

    Page<Company> findByStatus(
            CompanyStatus status,
            Pageable pageable
    );

    List<Company> findByOwnerAndStatus(
            User owner,
            CompanyStatus status
    );

    Page<Company> findByOwnerAndStatus(
            User owner,
            CompanyStatus status,
            Pageable pageable
    );

    Optional<Company> findByIdAndOwner(
            Long id,
            User owner
    );

    boolean existsByIdAndOwner(
            Long id,
            User owner
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    long countByOwner(User owner);

    long countByStatus(CompanyStatus status);

    long countByOwnerAndStatus(
            User owner,
            CompanyStatus status
    );
}