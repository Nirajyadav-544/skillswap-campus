package com.skillswap.college.repository;

import com.skillswap.college.entity.College;
import com.skillswap.college.entity.CollegeMember;
import com.skillswap.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollegeMemberRepository extends JpaRepository<CollegeMember, Long> {

    List<CollegeMember> findByCollege(College college);

    Page<CollegeMember> findByCollege(
            College college,
            Pageable pageable
    );

    List<CollegeMember> findByUser(User user);

    Page<CollegeMember> findByUser(
            User user,
            Pageable pageable
    );

    List<CollegeMember> findByCollegeAndActive(
            College college,
            Boolean active
    );

    Page<CollegeMember> findByCollegeAndActive(
            College college,
            Boolean active,
            Pageable pageable
    );

    Optional<CollegeMember> findByCollegeAndUser(
            College college,
            User user
    );

    Optional<CollegeMember> findByCollegeIdAndUserId(
            Long collegeId,
            Long userId
    );

    boolean existsByCollegeAndUser(
            College college,
            User user
    );

    boolean existsByCollegeIdAndUserId(
            Long collegeId,
            Long userId
    );

    boolean existsByCollegeAndUserAndActive(
            College college,
            User user,
            Boolean active
    );

    long countByCollege(College college);

    long countByCollegeAndActive(
            College college,
            Boolean active
    );
}