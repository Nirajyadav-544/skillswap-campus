package com.skillswap.profile.repository;

import com.skillswap.identity.entity.User;
import com.skillswap.profile.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository
        extends JpaRepository<StudentProfile, Long> {

    /**
     * Find the profile belonging to a specific user.
     *
     * Because every user can have only one student profile,
     * this returns Optional<StudentProfile>.
     */
    Optional<StudentProfile> findByUser(User user);

    /**
     * Find a profile directly using the user's ID.
     *
     * This is useful for authenticated endpoints where
     * we already know the logged-in user's ID.
     */
    Optional<StudentProfile> findByUserId(Long userId);

    /**
     * Check whether a user already has a student profile.
     */
    boolean existsByUser(User user);

    /**
     * Check whether a user ID already has a student profile.
     */
    boolean existsByUserId(Long userId);

    /**
     * Find profiles by college.
     *
     * Useful later for campus/community discovery.
     */
    List<StudentProfile> findAllByCollegeIgnoreCase(String college);

    /**
     * Find profiles by department.
     */
    List<StudentProfile> findAllByDepartmentIgnoreCase(String department);

    /**
     * Find profiles by course.
     */
    List<StudentProfile> findAllByCourseIgnoreCase(String course);

    /**
     * Find profiles by graduation year.
     */
    List<StudentProfile> findAllByGraduationYear(Integer graduationYear);

    /**
     * Find completed profiles.
     *
     * This can later be used by discovery,
     * matching and recommendation features.
     */
    List<StudentProfile> findAllByProfileCompletedTrue();

    /**
     * Find profiles belonging to a particular college
     * and department.
     */
    List<StudentProfile> findAllByCollegeIgnoreCaseAndDepartmentIgnoreCase(
            String college,
            String department
    );

    /**
     * Find completed profiles from a particular college.
     */
    List<StudentProfile> findAllByCollegeIgnoreCaseAndProfileCompletedTrue(
            String college
    );

    /**
     * Find completed profiles from a particular department.
     */
    List<StudentProfile> findAllByDepartmentIgnoreCaseAndProfileCompletedTrue(
            String department
    );
}