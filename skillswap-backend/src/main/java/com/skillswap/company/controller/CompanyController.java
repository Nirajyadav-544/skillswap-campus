package com.skillswap.company.controller;

import com.skillswap.company.dto.CompanyResponse;
import com.skillswap.company.dto.CreateCompanyRequest;
import com.skillswap.company.dto.UpdateCompanyRequest;
import com.skillswap.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(
            CompanyService companyService
    ) {
        this.companyService = companyService;
    }

    @PostMapping("/owner/{userId}")
    public ResponseEntity<CompanyResponse> create(
            @PathVariable Long userId,
            @Valid @RequestBody CreateCompanyRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        companyService.create(
                                userId,
                                request
                        )
                );
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getById(
            @PathVariable Long companyId
    ) {

        return ResponseEntity.ok(
                companyService.getById(companyId)
        );
    }

    @GetMapping("/owner/{userId}")
    public ResponseEntity<Page<CompanyResponse>> getMyCompanies(
            @PathVariable Long userId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                companyService.getMyCompanies(
                        userId,
                        pageable
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<Page<CompanyResponse>> getActiveCompanies(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                companyService.getActiveCompanies(pageable)
        );
    }

    @PutMapping("/{companyId}/owner/{userId}")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable Long companyId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateCompanyRequest request
    ) {

        return ResponseEntity.ok(
                companyService.update(
                        userId,
                        companyId,
                        request
                )
        );
    }

    @PatchMapping("/{companyId}/owner/{userId}/activate")
    public ResponseEntity<CompanyResponse> activate(
            @PathVariable Long companyId,
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                companyService.activate(
                        userId,
                        companyId
                )
        );
    }

    @PatchMapping("/{companyId}/owner/{userId}/suspend")
    public ResponseEntity<CompanyResponse> suspend(
            @PathVariable Long companyId,
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                companyService.suspend(
                        userId,
                        companyId
                )
        );
    }

    @PatchMapping("/{companyId}/owner/{userId}/archive")
    public ResponseEntity<CompanyResponse> archive(
            @PathVariable Long companyId,
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                companyService.archive(
                        userId,
                        companyId
                )
        );
    }

    @DeleteMapping("/{companyId}/owner/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long companyId,
            @PathVariable Long userId
    ) {

        companyService.delete(
                userId,
                companyId
        );

        return ResponseEntity.noContent().build();
    }
}