package com.skillswap.college.service;

import com.skillswap.college.dto.CollegeMemberResponse;
import com.skillswap.college.entity.College;
import com.skillswap.college.entity.CollegeMember;
import com.skillswap.college.mapper.CollegeMemberMapper;
import com.skillswap.college.repository.CollegeMemberRepository;
import com.skillswap.college.repository.CollegeRepository;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CollegeMemberService {

    private final CollegeMemberRepository collegeMemberRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final CollegeMemberMapper collegeMemberMapper;

    public CollegeMemberService(
            CollegeMemberRepository collegeMemberRepository,
            CollegeRepository collegeRepository,
            UserRepository userRepository,
            CollegeMemberMapper collegeMemberMapper
    ) {
        this.collegeMemberRepository = collegeMemberRepository;
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.collegeMemberMapper = collegeMemberMapper;
    }

    @Transactional(readOnly = true)
    public List<CollegeMemberResponse> getMembers(Long collegeId) {
        College college = getCollege(collegeId);

        return collegeMemberRepository.findByCollege(college)
                .stream()
                .map(collegeMemberMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CollegeMemberResponse> getMembers(
            Long collegeId,
            Pageable pageable
    ) {
        College college = getCollege(collegeId);

        return collegeMemberRepository.findByCollege(
                        college,
                        pageable
                )
                .map(collegeMemberMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<CollegeMemberResponse> getActiveMembers(Long collegeId) {
        College college = getCollege(collegeId);

        return collegeMemberRepository.findByCollegeAndActive(
                        college,
                        true
                )
                .stream()
                .map(collegeMemberMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CollegeMemberResponse> getActiveMembers(
            Long collegeId,
            Pageable pageable
    ) {
        College college = getCollege(collegeId);

        return collegeMemberRepository.findByCollegeAndActive(
                        college,
                        true,
                        pageable
                )
                .map(collegeMemberMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CollegeMemberResponse getMember(
            Long collegeId,
            Long userId
    ) {
        CollegeMember member =
                collegeMemberRepository.findByCollegeIdAndUserId(
                        collegeId,
                        userId
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "College member not found"
                        )
                );

        return collegeMemberMapper.toResponse(member);
    }

    public CollegeMemberResponse addMember(
            Long collegeId,
            Long userId,
            String role
    ) {
        College college = getCollege(collegeId);
        User user = getUser(userId);

        if (collegeMemberRepository.existsByCollegeAndUser(
                college,
                user
        )) {
            throw new IllegalArgumentException(
                    "User is already a member of this college"
            );
        }

        CollegeMember member = new CollegeMember(
                college,
                user,
                role
        );

        CollegeMember saved =
                collegeMemberRepository.save(member);

        return collegeMemberMapper.toResponse(saved);
    }

    public CollegeMemberResponse updateMemberRole(
            Long collegeId,
            Long userId,
            String role
    ) {
        CollegeMember member =
                collegeMemberRepository.findByCollegeIdAndUserId(
                        collegeId,
                        userId
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "College member not found"
                        )
                );

        member.setRole(role);

        CollegeMember saved =
                collegeMemberRepository.save(member);

        return collegeMemberMapper.toResponse(saved);
    }

    public void deactivateMember(
            Long collegeId,
            Long userId
    ) {
        CollegeMember member =
                collegeMemberRepository.findByCollegeIdAndUserId(
                        collegeId,
                        userId
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "College member not found"
                        )
                );

        member.setActive(false);

        collegeMemberRepository.save(member);
    }

    public void activateMember(
            Long collegeId,
            Long userId
    ) {
        CollegeMember member =
                collegeMemberRepository.findByCollegeIdAndUserId(
                        collegeId,
                        userId
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "College member not found"
                        )
                );

        member.setActive(true);

        collegeMemberRepository.save(member);
    }

    public void deleteMember(
            Long collegeId,
            Long userId
    ) {
        CollegeMember member =
                collegeMemberRepository.findByCollegeIdAndUserId(
                        collegeId,
                        userId
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "College member not found"
                        )
                );

        collegeMemberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public long countMembers(Long collegeId) {
        College college = getCollege(collegeId);

        return collegeMemberRepository.countByCollege(college);
    }

    @Transactional(readOnly = true)
    public long countActiveMembers(Long collegeId) {
        College college = getCollege(collegeId);

        return collegeMemberRepository.countByCollegeAndActive(
                college,
                true
        );
    }

    private College getCollege(Long collegeId) {
        return collegeRepository.findById(collegeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "College not found"
                        )
                );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }
}