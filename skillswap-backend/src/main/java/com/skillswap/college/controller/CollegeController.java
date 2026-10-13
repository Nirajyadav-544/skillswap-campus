package com.skillswap.college.controller;

import com.skillswap.college.dto.CollegeResponse;
import com.skillswap.college.dto.CreateCollegeRequest;
import com.skillswap.college.dto.UpdateCollegeRequest;
import com.skillswap.college.service.CollegeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(
            CollegeService collegeService
    ) {
        this.collegeService = collegeService;
    }

    @PostMapping("/owner/{userId}")
    public ResponseEntity<CollegeResponse> create(
            @PathVariable Long userId,
            @Valid @RequestBody CreateCollegeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        collegeService.create(
                                userId,
                                request
                        )
                );
    }

    @GetMapping("/{collegeId}")
    public ResponseEntity<CollegeResponse> getById(
            @PathVariable Long collegeId
    ) {
        return ResponseEntity.ok(
                collegeService.getById(collegeId)
        );
    }

    @GetMapping("/owner/{userId}")
    public ResponseEntity<Page<CollegeResponse>> getMyColleges(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        return ResponseEntity.ok(
                collegeService.getMyColleges(
                        userId,
                        pageable
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<Page<CollegeResponse>> getActiveColleges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        return ResponseEntity.ok(
                collegeService.getActiveColleges(
                        pageable
                )
        );
    }

    @PutMapping("/{collegeId}/owner/{userId}")
    public ResponseEntity<CollegeResponse> update(
            @PathVariable Long collegeId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateCollegeRequest request
    ) {
        return ResponseEntity.ok(
                collegeService.update(
                        userId,
                        collegeId,
                        request
                )
        );
    }

    @PatchMapping("/{collegeId}/owner/{userId}/activate")
    public ResponseEntity<CollegeResponse> activate(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                collegeService.activate(
                        userId,
                        collegeId
                )
        );
    }

    @PatchMapping("/{collegeId}/owner/{userId}/suspend")
    public ResponseEntity<CollegeResponse> suspend(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                collegeService.suspend(
                        userId,
                        collegeId
                )
        );
    }

    @PatchMapping("/{collegeId}/owner/{userId}/archive")
    public ResponseEntity<CollegeResponse> archive(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                collegeService.archive(
                        userId,
                        collegeId
                )
        );
    }

    @DeleteMapping("/{collegeId}/owner/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long collegeId,
            @PathVariable Long userId
    ) {
        collegeService.delete(
                userId,
                collegeId
        );

        return ResponseEntity.noContent().build();
    }
}