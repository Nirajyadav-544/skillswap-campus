package com.skillswap.activity.controller;

import com.skillswap.activity.dto.ActivityResponse;
import com.skillswap.activity.service.ActivityService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(
            ActivityService activityService
    ) {
        this.activityService = activityService;
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ActivityResponse>> getMyActivities(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                activityService.getMyActivities(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/mine/page")
    public ResponseEntity<Page<ActivityResponse>> getMyActivitiesPage(
            Authentication authentication,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            int size
    ) {

        return ResponseEntity.ok(
                activityService.getMyActivities(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }
}