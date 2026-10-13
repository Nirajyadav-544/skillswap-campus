package com.skillswap.college.controller;

import com.skillswap.college.dto.CollegeMemberResponse;
import com.skillswap.college.service.CollegeMemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colleges/{collegeId}/members")
public class CollegeMemberController {

    private final CollegeMemberService collegeMemberService;

    public CollegeMemberController(
            CollegeMemberService collegeMemberService
    ) {
        this.collegeMemberService = collegeMemberService;
    }

    @GetMapping
    public ResponseEntity<List<CollegeMemberResponse>> getMembers(
            @PathVariable Long collegeId
    ) {
        return ResponseEntity.ok(
                collegeMemberService.getMembers(collegeId)
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<CollegeMemberResponse>> getMembersPage(
            @PathVariable Long collegeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "joinedAt")
        );

        return ResponseEntity.ok(
                collegeMemberService.getMembers(
                        collegeId,
                        pageable
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<CollegeMemberResponse>> getActiveMembers(
            @PathVariable Long collegeId
    ) {
        return ResponseEntity.ok(
                collegeMemberService.getActiveMembers(collegeId)
        );
    }

    @GetMapping("/active/page")
    public ResponseEntity<Page<CollegeMemberResponse>> getActiveMembersPage(
            @PathVariable Long collegeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "joinedAt")
        );

        return ResponseEntity.ok(
                collegeMemberService.getActiveMembers(
                        collegeId,
                        pageable
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CollegeMemberResponse> getMember(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                collegeMemberService.getMember(
                        collegeId,
                        userId
                )
        );
    }

    @PostMapping
    public ResponseEntity<CollegeMemberResponse> addMember(
            @PathVariable Long collegeId,
            @RequestParam Long userId,
            @RequestParam String role
    ) {
        CollegeMemberResponse response =
                collegeMemberService.addMember(
                        collegeId,
                        userId,
                        role
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<CollegeMemberResponse> updateMemberRole(
            @PathVariable Long collegeId,
            @PathVariable Long userId,
            @RequestParam String role
    ) {
        return ResponseEntity.ok(
                collegeMemberService.updateMemberRole(
                        collegeId,
                        userId,
                        role
                )
        );
    }

    @PatchMapping("/{userId}/activate")
    public ResponseEntity<Void> activateMember(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        collegeMemberService.activateMember(
                collegeId,
                userId
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateMember(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        collegeMemberService.deactivateMember(
                collegeId,
                userId
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        collegeMemberService.deleteMember(
                collegeId,
                userId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countMembers(
            @PathVariable Long collegeId
    ) {
        return ResponseEntity.ok(
                collegeMemberService.countMembers(collegeId)
        );
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveMembers(
            @PathVariable Long collegeId
    ) {
        return ResponseEntity.ok(
                collegeMemberService.countActiveMembers(collegeId)
        );
    }
}