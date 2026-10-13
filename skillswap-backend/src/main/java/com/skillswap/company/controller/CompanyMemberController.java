package com.skillswap.company.controller;

import com.skillswap.company.dto.AddCompanyMemberRequest;
import com.skillswap.company.dto.CompanyMemberResponse;
import com.skillswap.company.service.CompanyMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyMemberController {

    private final CompanyMemberService companyMemberService;

    public CompanyMemberController(
            CompanyMemberService companyMemberService
    ) {
        this.companyMemberService = companyMemberService;
    }

    @PostMapping("/{companyId}/members/owner/{ownerId}")
    public ResponseEntity<CompanyMemberResponse> addMember(
            @PathVariable Long companyId,
            @PathVariable Long ownerId,
            @Valid @RequestBody AddCompanyMemberRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        companyMemberService.addMember(
                                ownerId,
                                companyId,
                                request
                        )
                );
    }

    @GetMapping("/{companyId}/members")
    public ResponseEntity<List<CompanyMemberResponse>> getMembers(
            @PathVariable Long companyId
    ) {

        return ResponseEntity.ok(
                companyMemberService.getMembers(companyId)
        );
    }

    @GetMapping("/{companyId}/members/{memberId}")
    public ResponseEntity<CompanyMemberResponse> getMember(
            @PathVariable Long companyId,
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                companyMemberService.getMember(
                        companyId,
                        memberId
                )
        );
    }

    @PatchMapping(
            "/{companyId}/members/{memberId}/owner/{ownerId}/activate"
    )
    public ResponseEntity<CompanyMemberResponse> activateMember(
            @PathVariable Long companyId,
            @PathVariable Long memberId,
            @PathVariable Long ownerId
    ) {

        return ResponseEntity.ok(
                companyMemberService.activateMember(
                        ownerId,
                        companyId,
                        memberId
                )
        );
    }

    @PatchMapping(
            "/{companyId}/members/{memberId}/owner/{ownerId}/deactivate"
    )
    public ResponseEntity<CompanyMemberResponse> deactivateMember(
            @PathVariable Long companyId,
            @PathVariable Long memberId,
            @PathVariable Long ownerId
    ) {

        return ResponseEntity.ok(
                companyMemberService.deactivateMember(
                        ownerId,
                        companyId,
                        memberId
                )
        );
    }

    @DeleteMapping(
            "/{companyId}/members/{memberId}/owner/{ownerId}"
    )
    public ResponseEntity<Void> removeMember(
            @PathVariable Long companyId,
            @PathVariable Long memberId,
            @PathVariable Long ownerId
    ) {

        companyMemberService.removeMember(
                ownerId,
                companyId,
                memberId
        );

        return ResponseEntity.noContent().build();
    }
}