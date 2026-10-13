package com.skillswap.resume.service;

import com.skillswap.common.exception.BadRequestException;
import com.skillswap.identity.entity.User;
import com.skillswap.identity.repository.UserRepository;
import com.skillswap.resume.dto.ResumeAnalysisRequest;
import com.skillswap.resume.dto.ResumeAnalysisResponse;
import com.skillswap.resume.entity.Resume;
import com.skillswap.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeAnalysisService(
            ResumeRepository resumeRepository,
            UserRepository userRepository
    ) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    public ResumeAnalysisResponse analyze(
            ResumeAnalysisRequest request,
            String userEmail
    ) {

        if (request == null) {
            throw new BadRequestException(
                    "Resume analysis request is required"
            );
        }

        request.normalize();

        User user = getUser(userEmail);

        Resume resume = null;

        if (request.getResumeId() != null) {

            resume = resumeRepository
                    .findByIdAndUser(
                            request.getResumeId(),
                            user
                    )
                    .orElseThrow(() ->
                            new BadRequestException(
                                    "Resume not found"
                            )
                    );
        }

        String resumeText = request.getResumeText();

        if ((resumeText == null || resumeText.isBlank())
                && resume != null) {

            resumeText = resume.getExtractedText();
        }

        if (resumeText == null || resumeText.isBlank()) {
            throw new BadRequestException(
                    "Resume text is required for analysis"
            );
        }

        String normalizedText =
                resumeText.toLowerCase(Locale.ROOT);

        ResumeAnalysisResponse response =
                ResumeAnalysisResponse.success(
                        "Resume analysis completed"
                );

        if (resume != null) {
            response.setResumeId(resume.getId());
        }

        response.setTargetCareer(
                request.getTargetCareer()
        );

        // =====================================================
        // DETECTED SKILLS
        // =====================================================

        List<String> detectedSkills =
                detectSkills(normalizedText);

        response.setDetectedSkills(
                detectedSkills
        );

        // =====================================================
        // STRENGTHS
        // =====================================================

        List<String> strengths =
                buildStrengths(
                        normalizedText,
                        detectedSkills
                );

        response.setStrengths(
                String.join("; ", strengths)
        );

        // =====================================================
        // SKILL GAPS
        // =====================================================

        List<String> skillGaps =
                buildSkillGaps(
                        normalizedText,
                        request.getTargetCareer()
                );

        response.setSkillGaps(
                skillGaps
        );

        double skillsScore =
                calculateSkillsScore(
                        detectedSkills,
                        skillGaps
                );

        response.setSkillsScore(
                skillsScore
        );

        // =====================================================
        // RECOMMENDED SKILLS
        // =====================================================

        List<String> recommendedSkills =
                buildRecommendedSkills(
                        request.getTargetCareer(),
                        detectedSkills
                );

        // =====================================================
        // RECOMMENDED PROJECTS
        // =====================================================

        List<String> recommendedProjects =
                buildRecommendedProjects(
                        request.getTargetCareer()
                );

        // =====================================================
        // RECOMMENDED CERTIFICATIONS
        // =====================================================

        List<String> recommendedCertifications =
                buildRecommendedCertifications(
                        request.getTargetCareer()
                );

        // =====================================================
        // COMBINE ALL RECOMMENDATIONS
        // =====================================================

        List<String> allRecommendations =
                new ArrayList<>();

        allRecommendations.addAll(
                recommendedSkills
        );

        allRecommendations.addAll(
                recommendedProjects
        );

        allRecommendations.addAll(
                recommendedCertifications
        );

        response.setRecommendations(
                String.join(
                        "; ",
                        allRecommendations
                )
        );

        // =====================================================
        // JOB KEYWORDS
        // =====================================================

        List<String> missingKeywords =
                buildMissingKeywords(
                        request.getJobDescription(),
                        normalizedText
                );

        response.setMissingKeywords(
                missingKeywords
        );

        // =====================================================
        // FORMATTING
        // =====================================================

        List<String> formattingSuggestions =
                buildFormattingSuggestions(
                        normalizedText
                );

        response.setFormattingSuggestions(
                formattingSuggestions
        );

        // =====================================================
        // EXPERIENCE
        // =====================================================

        List<String> experienceSuggestions =
                buildExperienceSuggestions(
                        normalizedText
                );

        double experienceScore =
                calculateExperienceScore(
                        normalizedText
                );

        response.setExperienceScore(
                experienceScore
        );

        // =====================================================
        // EDUCATION
        // =====================================================

        List<String> educationSuggestions =
                buildEducationSuggestions(
                        normalizedText
                );

        double educationScore =
                calculateEducationScore(
                        normalizedText
                );

        response.setEducationScore(
                educationScore
        );

        // =====================================================
        // IMPROVEMENTS
        // =====================================================

        List<String> improvementSuggestions =
                buildImprovementSuggestions(
                        normalizedText,
                        skillGaps,
                        missingKeywords
                );

        response.setImprovementSuggestions(
                improvementSuggestions
        );

        // =====================================================
        // ATS SCORE
        // =====================================================

        double atsScore =
                calculateAtsScore(
                        normalizedText,
                        request.getJobDescription()
                );

        response.setAtsScore(
                atsScore
        );

        // =====================================================
        // CAREER MATCH
        // =====================================================

        double careerMatch =
                calculateCareerMatch(
                        request.getTargetCareer(),
                        detectedSkills
                );

        response.setCareerMatchPercentage(
                careerMatch
        );

        // =====================================================
        // OVERALL SCORE
        // =====================================================

        double overallScore =
                calculateOverallScore(
                        atsScore,
                        careerMatch,
                        skillsScore,
                        experienceScore,
                        educationScore
                );

        response.setOverallScore(
                overallScore
        );

        // =====================================================
        // SAVE ANALYSIS SCORE ON RESUME
        // =====================================================

        if (resume != null) {

            resume.markAnalyzed(
                    overallScore
            );

            resumeRepository.save(
                    resume
            );
        }

        return response;
    }

    // =========================================================
    // SKILL DETECTION
    // =========================================================

    private List<String> detectSkills(
            String text
    ) {

        List<String> knownSkills = Arrays.asList(
                "Java",
                "Spring Boot",
                "Spring",
                "Python",
                "JavaScript",
                "TypeScript",
                "React",
                "Angular",
                "Vue",
                "Node.js",
                "HTML",
                "CSS",
                "SQL",
                "MySQL",
                "PostgreSQL",
                "MongoDB",
                "Redis",
                "Docker",
                "Kubernetes",
                "AWS",
                "Azure",
                "Git",
                "GitHub",
                "REST API",
                "Microservices",
                "Machine Learning",
                "Artificial Intelligence",
                "Data Science",
                "TensorFlow",
                "PyTorch",
                "Figma",
                "UI/UX",
                "Communication",
                "Leadership"
        );

        List<String> detected =
                new ArrayList<>();

        for (String skill : knownSkills) {

            if (containsSkill(
                    text,
                    skill
            )) {
                detected.add(skill);
            }
        }

        return detected;
    }

    private boolean containsSkill(
            String text,
            String skill
    ) {

        String normalizedSkill =
                skill.toLowerCase(Locale.ROOT);

        return text.contains(
                normalizedSkill
        );
    }

    private boolean containsSkill(
            List<String> skills,
            String target
    ) {

        if (skills == null || skills.isEmpty()) {
            return false;
        }

        return skills.stream()
                .anyMatch(
                        skill ->
                                skill.equalsIgnoreCase(
                                        target
                                )
                );
    }

    // =========================================================
    // STRENGTHS
    // =========================================================

    private List<String> buildStrengths(
            String text,
            List<String> detectedSkills
    ) {

        List<String> strengths =
                new ArrayList<>();

        if (!detectedSkills.isEmpty()) {

            strengths.add(
                    "Resume contains identifiable technical or professional skills."
            );
        }

        if (text.contains("project")
                || text.contains("projects")) {

            strengths.add(
                    "Resume includes project experience."
            );
        }

        if (text.contains("experience")
                || text.contains("internship")
                || text.contains("work experience")) {

            strengths.add(
                    "Resume contains professional or internship experience."
            );
        }

        if (text.contains("education")
                || text.contains("university")
                || text.contains("college")
                || text.contains("degree")) {

            strengths.add(
                    "Resume contains education information."
            );
        }

        if (text.contains("github")
                || text.contains("linkedin")
                || text.contains("portfolio")) {

            strengths.add(
                    "Resume contains professional online presence information."
            );
        }

        if (strengths.isEmpty()) {

            strengths.add(
                    "Resume content was successfully extracted for further improvement."
            );
        }

        return strengths;
    }

    // =========================================================
    // SKILL GAPS
    // =========================================================

    private List<String> buildSkillGaps(
            String text,
            String targetCareer
    ) {

        List<String> gaps =
                new ArrayList<>();

        String career =
                targetCareer == null
                        ? ""
                        : targetCareer.toLowerCase(
                                Locale.ROOT
                        );

        if (career.contains("java")
                || career.contains("backend")
                || career.contains("software")) {

            addIfMissing(
                    gaps,
                    text,
                    "Spring Boot",
                    "Consider adding Spring Boot experience."
            );

            addIfMissing(
                    gaps,
                    text,
                    "SQL",
                    "Consider adding SQL/database experience."
            );

            addIfMissing(
                    gaps,
                    text,
                    "REST API",
                    "Consider adding REST API development experience."
            );

            addIfMissing(
                    gaps,
                    text,
                    "Docker",
                    "Consider adding Docker/containerization experience."
            );
        }

        if (career.contains("frontend")
                || career.contains("react")
                || career.contains("web")) {

            addIfMissing(
                    gaps,
                    text,
                    "React",
                    "Consider adding React experience."
            );

            addIfMissing(
                    gaps,
                    text,
                    "TypeScript",
                    "Consider adding TypeScript experience."
            );

            addIfMissing(
                    gaps,
                    text,
                    "CSS",
                    "Consider adding CSS/UI implementation experience."
            );
        }

        if (career.contains("data")
                || career.contains("machine learning")
                || career.contains("ml")
                || career.contains("ai")) {

            addIfMissing(
                    gaps,
                    text,
                    "Python",
                    "Consider adding Python experience."
            );

            addIfMissing(
                    gaps,
                    text,
                    "Machine Learning",
                    "Consider adding machine-learning experience."
            );

            addIfMissing(
                    gaps,
                    text,
                    "SQL",
                    "Consider adding SQL/data-querying experience."
            );
        }

        return gaps;
    }

    private void addIfMissing(
            List<String> list,
            String text,
            String keyword,
            String message
    ) {

        if (!text.contains(
                keyword.toLowerCase(Locale.ROOT)
        )) {

            list.add(message);
        }
    }

    // =========================================================
    // RECOMMENDED SKILLS
    // =========================================================

    private List<String> buildRecommendedSkills(
            String targetCareer,
            List<String> detectedSkills
    ) {

        List<String> recommendations =
                new ArrayList<>();

        String career =
                targetCareer == null
                        ? ""
                        : targetCareer.toLowerCase(
                                Locale.ROOT
                        );

        if (career.contains("java")
                || career.contains("backend")
                || career.contains("software")) {

            recommendations.add("Spring Boot");
            recommendations.add("REST API");
            recommendations.add("SQL");
            recommendations.add("Docker");
        }

        if (career.contains("frontend")
                || career.contains("react")
                || career.contains("web")) {

            recommendations.add("React");
            recommendations.add("TypeScript");
            recommendations.add("CSS");
        }

        if (career.contains("data")
                || career.contains("machine learning")
                || career.contains("ml")
                || career.contains("ai")) {

            recommendations.add("Python");
            recommendations.add("Machine Learning");
            recommendations.add("SQL");
        }

        recommendations.removeIf(
                skill -> detectedSkills.stream()
                        .anyMatch(
                                detected ->
                                        detected.equalsIgnoreCase(
                                                skill
                                        )
                        )
        );

        return recommendations.stream()
                .distinct()
                .toList();
    }

    // =========================================================
    // MISSING KEYWORDS
    // =========================================================

    private List<String> buildMissingKeywords(
            String jobDescription,
            String resumeText
    ) {

        List<String> missing =
                new ArrayList<>();

        if (jobDescription == null
                || jobDescription.isBlank()) {

            return missing;
        }

        List<String> keywords =
                Arrays.asList(
                        "java",
                        "spring boot",
                        "python",
                        "react",
                        "typescript",
                        "javascript",
                        "sql",
                        "mysql",
                        "postgresql",
                        "docker",
                        "kubernetes",
                        "aws",
                        "git",
                        "rest api",
                        "microservices",
                        "machine learning",
                        "communication",
                        "leadership"
                );

        String normalizedJob =
                jobDescription.toLowerCase(
                        Locale.ROOT
                );

        for (String keyword : keywords) {

            if (normalizedJob.contains(keyword)
                    && !resumeText.contains(keyword)) {

                missing.add(keyword);
            }
        }

        return missing;
    }

    // =========================================================
    // PROJECT RECOMMENDATIONS
    // =========================================================

    private List<String> buildRecommendedProjects(
            String targetCareer
    ) {

        List<String> projects =
                new ArrayList<>();

        String career =
                targetCareer == null
                        ? ""
                        : targetCareer.toLowerCase(
                                Locale.ROOT
                        );

        if (career.contains("java")
                || career.contains("backend")
                || career.contains("software")) {

            projects.add(
                    "Build a Spring Boot REST API with MySQL."
            );

            projects.add(
                    "Build a Dockerized backend application."
            );
        }

        if (career.contains("frontend")
                || career.contains("react")
                || career.contains("web")) {

            projects.add(
                    "Build a React and TypeScript dashboard."
            );
        }

        if (career.contains("data")
                || career.contains("machine learning")
                || career.contains("ai")) {

            projects.add(
                    "Build an end-to-end machine-learning project."
            );
        }

        if (projects.isEmpty()) {

            projects.add(
                    "Build a complete project demonstrating your target career skills."
            );
        }

        return projects;
    }

    // =========================================================
    // CERTIFICATIONS
    // =========================================================

    private List<String> buildRecommendedCertifications(
            String targetCareer
    ) {

        List<String> certifications =
                new ArrayList<>();

        String career =
                targetCareer == null
                        ? ""
                        : targetCareer.toLowerCase(
                                Locale.ROOT
                        );

        if (career.contains("cloud")
                || career.contains("aws")) {

            certifications.add(
                    "Consider an AWS certification relevant to your experience level."
            );
        }

        if (career.contains("java")
                || career.contains("backend")) {

            certifications.add(
                    "Consider a recognized Java/Spring or cloud certification."
            );
        }

        if (career.contains("data")
                || career.contains("ai")
                || career.contains("machine learning")) {

            certifications.add(
                    "Consider a recognized data/AI certification."
            );
        }

        return certifications;
    }

    // =========================================================
    // FORMATTING
    // =========================================================

    private List<String> buildFormattingSuggestions(
            String text
    ) {

        List<String> suggestions =
                new ArrayList<>();

        if (!text.contains("skills")) {

            suggestions.add(
                    "Add a clearly labeled Skills section."
            );
        }

        if (!text.contains("experience")) {

            suggestions.add(
                    "Add a clearly labeled Experience section."
            );
        }

        if (!text.contains("education")) {

            suggestions.add(
                    "Add a clearly labeled Education section."
            );
        }

        if (!text.contains("project")) {

            suggestions.add(
                    "Add a clearly labeled Projects section."
            );
        }

        suggestions.add(
                "Use consistent section headings and concise bullet points."
        );

        return suggestions;
    }

    // =========================================================
    // EXPERIENCE
    // =========================================================

    private List<String> buildExperienceSuggestions(
            String text
    ) {

        List<String> suggestions =
                new ArrayList<>();

        if (!text.contains("experience")
                && !text.contains("internship")) {

            suggestions.add(
                    "Add internship, employment, freelance, or relevant practical experience."
            );
        }

        suggestions.add(
                "Describe experience using measurable outcomes where possible."
        );

        return suggestions;
    }

    private double calculateExperienceScore(
            String text
    ) {

        double score = 40.0;

        if (text.contains("experience")) {
            score += 25.0;
        }

        if (text.contains("internship")) {
            score += 15.0;
        }

        if (text.contains("work")) {
            score += 10.0;
        }

        if (text.contains("achievement")
                || text.contains("achievements")) {

            score += 10.0;
        }

        return clamp(score);
    }

    // =========================================================
    // EDUCATION
    // =========================================================

    private List<String> buildEducationSuggestions(
            String text
    ) {

        List<String> suggestions =
                new ArrayList<>();

        if (!text.contains("education")
                && !text.contains("degree")
                && !text.contains("university")
                && !text.contains("college")) {

            suggestions.add(
                    "Add your relevant educational qualifications."
            );
        }

        return suggestions;
    }

    private double calculateEducationScore(
            String text
    ) {

        double score = 40.0;

        if (text.contains("education")) {
            score += 20.0;
        }

        if (text.contains("degree")) {
            score += 20.0;
        }

        if (text.contains("university")
                || text.contains("college")) {

            score += 15.0;
        }

        if (text.contains("gpa")
                || text.contains("cgpa")
                || text.contains("percentage")) {

            score += 5.0;
        }

        return clamp(score);
    }

    // =========================================================
    // IMPROVEMENTS
    // =========================================================

    private List<String> buildImprovementSuggestions(
            String text,
            List<String> skillGaps,
            List<String> missingKeywords
    ) {

        List<String> suggestions =
                new ArrayList<>();

        if (!skillGaps.isEmpty()) {

            suggestions.add(
                    "Address the identified skill gaps with projects or learning activities."
            );
        }

        if (!missingKeywords.isEmpty()) {

            suggestions.add(
                    "Review the job-specific keywords and add relevant experience where it is truthful."
            );
        }

        if (!text.contains("achievement")
                && !text.contains("achievements")) {

            suggestions.add(
                    "Highlight measurable achievements instead of listing responsibilities only."
            );
        }

        suggestions.add(
                "Keep resume content concise, relevant, and tailored to the target role."
        );

        return suggestions;
    }

    // =========================================================
    // ATS SCORE
    // =========================================================

    private double calculateAtsScore(
            String resumeText,
            String jobDescription
    ) {

        double score = 50.0;

        if (resumeText.contains("skills")) {
            score += 8;
        }

        if (resumeText.contains("experience")) {
            score += 8;
        }

        if (resumeText.contains("education")) {
            score += 8;
        }

        if (resumeText.contains("project")) {
            score += 8;
        }

        if (resumeText.contains("github")
                || resumeText.contains("linkedin")
                || resumeText.contains("portfolio")) {

            score += 5;
        }

        if (jobDescription != null
                && !jobDescription.isBlank()) {

            List<String> missing =
                    buildMissingKeywords(
                            jobDescription,
                            resumeText
                    );

            score -= Math.min(
                    missing.size() * 3.0,
                    20.0
            );
        }

        return clamp(score);
    }

    // =========================================================
    // SKILLS SCORE
    // =========================================================

    private double calculateSkillsScore(
            List<String> detectedSkills,
            List<String> skillGaps
    ) {

        int detected =
                detectedSkills == null
                        ? 0
                        : detectedSkills.size();

        int gaps =
                skillGaps == null
                        ? 0
                        : skillGaps.size();

        if (detected == 0 && gaps == 0) {
            return 0.0;
        }

        double score =
                50.0
                        + (detected * 5.0)
                        - (gaps * 5.0);

        return clamp(score);
    }

    // =========================================================
    // CAREER MATCH
    // =========================================================

    private double calculateCareerMatch(
            String targetCareer,
            List<String> detectedSkills
    ) {

        if (targetCareer == null
                || targetCareer.isBlank()) {

            return 0.0;
        }

        String career =
                targetCareer.toLowerCase(
                        Locale.ROOT
                );

        int matched = 0;
        int expected = 0;

        if (career.contains("java")
                || career.contains("backend")
                || career.contains("software")) {

            expected += 4;

            if (containsSkill(
                    detectedSkills,
                    "Java"
            )) {
                matched++;
            }

            if (containsSkill(
                    detectedSkills,
                    "Spring Boot"
            )) {
                matched++;
            }

            if (containsSkill(
                    detectedSkills,
                    "SQL"
            )) {
                matched++;
            }

            if (containsSkill(
                    detectedSkills,
                    "REST API"
            )) {
                matched++;
            }
        }

        if (career.contains("frontend")
                || career.contains("react")
                || career.contains("web")) {

            expected += 3;

            if (containsSkill(
                    detectedSkills,
                    "React"
            )) {
                matched++;
            }

            if (containsSkill(
                    detectedSkills,
                    "TypeScript"
            )) {
                matched++;
            }

            if (containsSkill(
                    detectedSkills,
                    "CSS"
            )) {
                matched++;
            }
        }

        if (career.contains("data")
                || career.contains("machine learning")
                || career.contains("ml")
                || career.contains("ai")) {

            expected += 3;

            if (containsSkill(
                    detectedSkills,
                    "Python"
            )) {
                matched++;
            }

            if (containsSkill(
                    detectedSkills,
                    "Machine Learning"
            )) {
                matched++;
            }

            if (containsSkill(
                    detectedSkills,
                    "SQL"
            )) {
                matched++;
            }
        }

        if (expected == 0) {

            return detectedSkills.isEmpty()
                    ? 0.0
                    : Math.min(
                            100.0,
                            40.0
                                    + detectedSkills.size()
                                    * 5.0
                    );
        }

        return clamp(
                ((double) matched / expected)
                        * 100.0
        );
    }

    // =========================================================
    // OVERALL SCORE
    // =========================================================

    private double calculateOverallScore(
            double atsScore,
            double careerMatch,
            double skillsScore,
            double experienceScore,
            double educationScore
    ) {

        double score =
                atsScore * 0.35
                        + careerMatch * 0.30
                        + skillsScore * 0.15
                        + experienceScore * 0.10
                        + educationScore * 0.10;

        return clamp(score);
    }

    // =========================================================
    // CLAMP
    // =========================================================

    private double clamp(
            double value
    ) {

        return Math.max(
                0.0,
                Math.min(
                        100.0,
                        value
                )
        );
    }

    // =========================================================
    // USER
    // =========================================================

    private User getUser(
            String userEmail
    ) {

        if (userEmail == null
                || userEmail.isBlank()) {

            throw new BadRequestException(
                    "User email is required"
            );
        }

        return userRepository
                .findByEmail(
                        userEmail.trim()
                )
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }
}