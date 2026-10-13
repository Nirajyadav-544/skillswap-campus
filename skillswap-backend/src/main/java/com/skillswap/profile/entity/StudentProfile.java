package com.skillswap.profile.entity;

import com.skillswap.identity.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_profiles",
        indexes = {
                @Index(name = "idx_student_profile_user_id", columnList = "user_id"),
                @Index(name = "idx_student_profile_college", columnList = "college"),
                @Index(name = "idx_student_profile_department", columnList = "department"),
                @Index(name = "idx_student_profile_graduation_year", columnList = "graduation_year")
        }
)
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * ============================================================
     * USER RELATIONSHIP
     * ============================================================
     *
     * Every student profile belongs to exactly one User.
     *
     * We intentionally do NOT cascade delete from Profile -> User.
     * The identity account owns the profile, not the other way around.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;


    /*
     * ============================================================
     * BASIC PROFILE INFORMATION
     * ============================================================
     */

    @Column(
            name = "headline",
            length = 200
    )
    private String headline;


    @Column(
            name = "bio",
            columnDefinition = "TEXT"
    )
    private String bio;


    /*
     * ============================================================
     * EDUCATION
     * ============================================================
     */

    @Column(
            name = "college",
            length = 200
    )
    private String college;


    @Column(
            name = "department",
            length = 150
    )
    private String department;


    @Column(
            name = "course",
            length = 150
    )
    private String course;


    @Column(
            name = "year_of_study"
    )
    private Integer yearOfStudy;


    @Column(
            name = "graduation_year"
    )
    private Integer graduationYear;


    /*
     * ============================================================
     * LOCATION
     * ============================================================
     */

    @Column(
            name = "location",
            length = 200
    )
    private String location;


    /*
     * ============================================================
     * PROFILE IMAGE
     * ============================================================
     *
     * We store only the URL/path here.
     * Actual file storage will be handled by a later media/storage
     * module instead of storing binary image data in MySQL.
     */

    @Column(
            name = "profile_image_url",
            length = 1000
    )
    private String profileImageUrl;


    /*
     * ============================================================
     * PROFESSIONAL / SOCIAL LINKS
     * ============================================================
     */

    @Column(
            name = "github_url",
            length = 500
    )
    private String githubUrl;


    @Column(
            name = "linkedin_url",
            length = 500
    )
    private String linkedinUrl;


    @Column(
            name = "portfolio_url",
            length = 500
    )
    private String portfolioUrl;


    /*
     * ============================================================
     * AVAILABILITY
     * ============================================================
     *
     * Kept as a simple text field for V1.
     * Later, if we need structured scheduling, this can become
     * its own availability domain without breaking the profile API.
     */

    @Column(
            name = "availability",
            length = 500
    )
    private String availability;


    /*
     * ============================================================
     * PROFILE STATUS
     * ============================================================
     */

    @Column(
            name = "profile_completed",
            nullable = false
    )
    private boolean profileCompleted = false;


    /*
     * ============================================================
     * AUDIT FIELDS
     * ============================================================
     */

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;


    /*
     * ============================================================
     * JPA LIFECYCLE
     * ============================================================
     */

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();
    }


    /*
     * ============================================================
     * CONSTRUCTORS
     * ============================================================
     */

    public StudentProfile() {
    }


    /*
     * ============================================================
     * GETTERS / SETTERS
     * ============================================================
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }


    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }


    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }


    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }


    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }


    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }


    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}