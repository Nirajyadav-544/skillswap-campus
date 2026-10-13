package com.skillswap.activity.service;

import com.skillswap.activity.dto.ActivityResponse;
import com.skillswap.activity.entity.Activity;
import com.skillswap.activity.entity.ActivityType;
import com.skillswap.activity.repository.ActivityRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final UserRepository userRepository;

    public ActivityService(
            ActivityRepository activityRepository,
            UserRepository userRepository
    ) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> getMyActivities(
            String email
    ) {

        User user = getUserByEmail(email);

        return activityRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(ActivityResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ActivityResponse> getMyActivities(
            String email,
            int page,
            int size
    ) {

        User user = getUserByEmail(email);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(
                Math.max(size, 1),
                100
        );

        Pageable pageable =
                PageRequest.of(
                        safePage,
                        safeSize
                );

        return activityRepository
                .findByUserOrderByCreatedAtDesc(
                        user,
                        pageable
                )
                .map(ActivityResponse::from);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> getMyActivitiesByType(
            String email,
            ActivityType type
    ) {

        User user = getUserByEmail(email);

        return activityRepository
                .findByUserAndTypeOrderByCreatedAtDesc(
                        user,
                        type
                )
                .stream()
                .map(ActivityResponse::from)
                .toList();
    }

    public ActivityResponse createActivity(
            User user,
            ActivityType type,
            String message,
            Long referenceId,
            String referenceType
    ) {

        Activity activity = new Activity();

        activity.setUser(user);
        activity.setType(type);
        activity.setMessage(message);
        activity.setReferenceId(referenceId);
        activity.setReferenceType(referenceType);

        return ActivityResponse.from(
                activityRepository.save(activity)
        );
    }

    public ActivityResponse createActivity(
            String email,
            ActivityType type,
            String message,
            Long referenceId,
            String referenceType
    ) {

        User user = getUserByEmail(email);

        return createActivity(
                user,
                type,
                message,
                referenceId,
                referenceType
        );
    }

    private User getUserByEmail(
            String email
    ) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }
}