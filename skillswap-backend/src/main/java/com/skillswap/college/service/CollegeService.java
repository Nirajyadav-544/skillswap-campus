package com.skillswap.college.service;

import com.skillswap.college.dto.CollegeResponse;
import com.skillswap.college.dto.CreateCollegeRequest;
import com.skillswap.college.dto.UpdateCollegeRequest;
import com.skillswap.college.entity.College;
import com.skillswap.college.entity.CollegeStatus;
import com.skillswap.college.repository.CollegeRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;

    public CollegeService(
            CollegeRepository collegeRepository,
            UserRepository userRepository
    ) {
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
    }

    public CollegeResponse create(
            Long userId,
            CreateCollegeRequest request
    ) {
        User owner = getUser(userId);

        if (request == null) {
            throw new IllegalArgumentException(
                    "College request is required"
            );
        }

        if (collegeRepository.existsByNameIgnoreCase(
                request.getName().trim()
        )) {
            throw new IllegalArgumentException(
                    "College with this name already exists"
            );
        }

        College college = new College(
                request.getName(),
                owner
        );

        applyCreateData(college, request);

        return CollegeResponse.from(
                collegeRepository.save(college)
        );
    }

    @Transactional(readOnly = true)
    public CollegeResponse getById(Long collegeId) {
        return CollegeResponse.from(getCollege(collegeId));
    }

    @Transactional(readOnly = true)
    public Page<CollegeResponse> getMyColleges(
            Long userId,
            Pageable pageable
    ) {
        User owner = getUser(userId);

        return collegeRepository
                .findByOwner(owner, pageable)
                .map(CollegeResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<CollegeResponse> getActiveColleges(
            Pageable pageable
    ) {
        return collegeRepository
                .findByStatus(
                        CollegeStatus.ACTIVE,
                        pageable
                )
                .map(CollegeResponse::from);
    }

    public CollegeResponse update(
            Long userId,
            Long collegeId,
            UpdateCollegeRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "College update request is required"
            );
        }

        College college =
                getOwnedCollege(userId, collegeId);

        if (request.getName() != null
                && !request.getName().trim().isEmpty()
                && !request.getName().trim()
                .equalsIgnoreCase(college.getName())) {

            if (collegeRepository.existsByNameIgnoreCase(
                    request.getName().trim()
            )) {
                throw new IllegalArgumentException(
                        "College with this name already exists"
                );
            }
        }

        applyUpdateData(college, request);

        return CollegeResponse.from(
                collegeRepository.save(college)
        );
    }

    public CollegeResponse activate(
            Long userId,
            Long collegeId
    ) {
        College college =
                getOwnedCollege(userId, collegeId);

        college.setStatus(CollegeStatus.ACTIVE);
        college.setVerified(true);

        return CollegeResponse.from(
                collegeRepository.save(college)
        );
    }

    public CollegeResponse suspend(
            Long userId,
            Long collegeId
    ) {
        College college =
                getOwnedCollege(userId, collegeId);

        college.setStatus(CollegeStatus.SUSPENDED);

        return CollegeResponse.from(
                collegeRepository.save(college)
        );
    }

    public CollegeResponse archive(
            Long userId,
            Long collegeId
    ) {
        College college =
                getOwnedCollege(userId, collegeId);

        college.setStatus(CollegeStatus.ARCHIVED);

        return CollegeResponse.from(
                collegeRepository.save(college)
        );
    }

    public void delete(
            Long userId,
            Long collegeId
    ) {
        College college =
                getOwnedCollege(userId, collegeId);

        collegeRepository.delete(college);
    }

    private College getCollege(Long collegeId) {
        if (collegeId == null) {
            throw new IllegalArgumentException(
                    "College ID is required"
            );
        }

        return collegeRepository.findById(collegeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "College not found"
                        )
                );
    }

    private College getOwnedCollege(
            Long userId,
            Long collegeId
    ) {
        User owner = getUser(userId);

        return collegeRepository
                .findByIdAndOwner(collegeId, owner)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "College not found or you are not the owner"
                        )
                );
    }

    private User getUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private void applyCreateData(
            College college,
            CreateCollegeRequest request
    ) {
        college.setLegalName(request.getLegalName());
        college.setDescription(request.getDescription());
        college.setWebsite(request.getWebsite());
        college.setLocation(request.getLocation());
        college.setCity(request.getCity());
        college.setState(request.getState());
        college.setCountry(request.getCountry());
        college.setPostalCode(request.getPostalCode());
        college.setLogoUrl(request.getLogoUrl());
        college.setEmail(request.getEmail());
        college.setPhoneNumber(request.getPhoneNumber());
    }

    private void applyUpdateData(
            College college,
            UpdateCollegeRequest request
    ) {
        if (request.getName() != null
                && !request.getName().trim().isEmpty()) {
            college.setName(request.getName());
        }

        if (request.getLegalName() != null) {
            college.setLegalName(request.getLegalName());
        }

        if (request.getDescription() != null) {
            college.setDescription(request.getDescription());
        }

        if (request.getWebsite() != null) {
            college.setWebsite(request.getWebsite());
        }

        if (request.getLocation() != null) {
            college.setLocation(request.getLocation());
        }

        if (request.getCity() != null) {
            college.setCity(request.getCity());
        }

        if (request.getState() != null) {
            college.setState(request.getState());
        }

        if (request.getCountry() != null) {
            college.setCountry(request.getCountry());
        }

        if (request.getPostalCode() != null) {
            college.setPostalCode(request.getPostalCode());
        }

        if (request.getLogoUrl() != null) {
            college.setLogoUrl(request.getLogoUrl());
        }

        if (request.getEmail() != null) {
            college.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null) {
            college.setPhoneNumber(request.getPhoneNumber());
        }
    }
}