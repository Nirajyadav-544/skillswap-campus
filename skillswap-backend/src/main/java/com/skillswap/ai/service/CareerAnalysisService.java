package com.skillswap.ai.service;

import com.skillswap.ai.dto.CareerAnalysisRequest;
import com.skillswap.ai.dto.CareerAnalysisResponse;
import com.skillswap.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class CareerAnalysisService {

    public CareerAnalysisResponse analyze(
            CareerAnalysisRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Career analysis request is required"
            );
        }

        request.normalize();

        if (request.getCareerGoal() == null
                || request.getCareerGoal().isBlank()) {
            throw new BadRequestException(
                    "Career goal is required"
            );
        }

        List<String> strengths =
                extractItems(request.getSkills());

        List<String> skillGaps =
                determineSkillGaps(
                        request.getCareerGoal(),
                        request.getSkills()
                );

        List<String> recommendedSkills =
                new ArrayList<>(skillGaps);

        List<String> recommendedProjects =
                recommendProjects(
                        request.getCareerGoal()
                );

        List<String> certifications =
                recommendCertifications(
                        request.getCareerGoal()
                );

        List<String> actions =
                recommendActions(
                        request,
                        skillGaps
                );

        int matchPercentage =
                calculateMatchPercentage(
                        request.getCareerGoal(),
                        request.getSkills()
                );

        return CareerAnalysisResponse.success(
                request.getCareerGoal(),
                matchPercentage,
                strengths,
                skillGaps,
                recommendedSkills,
                recommendedProjects,
                certifications,
                actions
        );
    }

    private List<String> extractItems(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(
                        value.split("[,;\\n]")
                )
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .distinct()
                .limit(20)
                .toList();
    }

    private List<String> determineSkillGaps(
            String careerGoal,
            String currentSkills
    ) {
        String goal =
                careerGoal
                        .toLowerCase(Locale.ROOT);

        List<String> requiredSkills;

        if (goal.contains("full stack")
                || goal.contains("fullstack")) {

            requiredSkills = List.of(
                    "Java",
                    "Spring Boot",
                    "JavaScript",
                    "React",
                    "SQL",
                    "Git",
                    "Docker"
            );

        } else if (goal.contains("frontend")
                || goal.contains("front end")
                || goal.contains("react")) {

            requiredSkills = List.of(
                    "HTML",
                    "CSS",
                    "JavaScript",
                    "React",
                    "TypeScript",
                    "Git"
            );

        } else if (goal.contains("backend")
                || goal.contains("back end")
                || goal.contains("java")) {

            requiredSkills = List.of(
                    "Java",
                    "Spring Boot",
                    "REST API",
                    "SQL",
                    "Git",
                    "Docker"
            );

        } else if (goal.contains("data")
                || goal.contains("machine learning")
                || goal.contains("ai")) {

            requiredSkills = List.of(
                    "Python",
                    "SQL",
                    "Data Science",
                    "Machine Learning",
                    "Statistics"
            );

        } else {
            requiredSkills = List.of(
                    "Communication",
                    "Problem Solving",
                    "Technical Skills",
                    "Git",
                    "Project Experience"
            );
        }

        List<String> gaps =
                new ArrayList<>();

        for (String required : requiredSkills) {

            if (!containsSkill(
                    currentSkills,
                    required
            )) {
                gaps.add(required);
            }
        }

        return gaps;
    }

    private int calculateMatchPercentage(
            String careerGoal,
            String currentSkills
    ) {
        List<String> gaps =
                determineSkillGaps(
                        careerGoal,
                        currentSkills
                );

        String goal =
                careerGoal
                        .toLowerCase(Locale.ROOT);

        int requiredCount;

        if (goal.contains("full stack")
                || goal.contains("fullstack")) {

            requiredCount = 7;

        } else if (goal.contains("frontend")
                || goal.contains("front end")
                || goal.contains("react")) {

            requiredCount = 6;

        } else if (goal.contains("backend")
                || goal.contains("back end")
                || goal.contains("java")) {

            requiredCount = 6;

        } else if (goal.contains("data")
                || goal.contains("machine learning")
                || goal.contains("ai")) {

            requiredCount = 5;

        } else {
            requiredCount = 5;
        }

        int matched =
                Math.max(
                        requiredCount - gaps.size(),
                        0
                );

        return (int) Math.round(
                matched * 100.0 / requiredCount
        );
    }

    private boolean containsSkill(
            String currentSkills,
            String requiredSkill
    ) {
        if (currentSkills == null
                || currentSkills.isBlank()) {
            return false;
        }

        String normalized =
                currentSkills
                        .toLowerCase(Locale.ROOT);

        String required =
                requiredSkill
                        .toLowerCase(Locale.ROOT);

        return normalized.contains(required);
    }

    private List<String> recommendProjects(
            String careerGoal
    ) {
        String goal =
                careerGoal
                        .toLowerCase(Locale.ROOT);

        if (goal.contains("full stack")
                || goal.contains("fullstack")) {

            return List.of(
                    "Student Skill Exchange Platform",
                    "E-Commerce Application",
                    "Learning Management System"
            );
        }

        if (goal.contains("frontend")
                || goal.contains("react")) {

            return List.of(
                    "Campus Dashboard",
                    "Project Management UI",
                    "Real-Time Chat Application"
            );
        }

        if (goal.contains("backend")
                || goal.contains("java")) {

            return List.of(
                    "REST API Platform",
                    "Authentication System",
                    "Campus Management Backend"
            );
        }

        if (goal.contains("data")
                || goal.contains("machine learning")
                || goal.contains("ai")) {

            return List.of(
                    "Student Skill Prediction System",
                    "Resume Skill Analyzer",
                    "Campus Recommendation System"
            );
        }

        return List.of(
                "Personal Portfolio Project",
                "Campus Collaboration Project",
                "Open Source Contribution"
        );
    }

    private List<String> recommendCertifications(
            String careerGoal
    ) {
        String goal =
                careerGoal
                        .toLowerCase(Locale.ROOT);

        if (goal.contains("cloud")
                || goal.contains("devops")) {

            return List.of(
                    "Cloud fundamentals certification",
                    "DevOps fundamentals certification"
            );
        }

        if (goal.contains("data")
                || goal.contains("machine learning")
                || goal.contains("ai")) {

            return List.of(
                    "Data science certification",
                    "Machine learning certification"
            );
        }

        if (goal.contains("java")
                || goal.contains("backend")
                || goal.contains("full stack")) {

            return List.of(
                    "Java certification",
                    "Spring development certification"
            );
        }

        return List.of(
                "Relevant technology certification",
                "Professional communication certification"
        );
    }

    private List<String> recommendActions(
            CareerAnalysisRequest request,
            List<String> skillGaps
    ) {
        List<String> actions =
                new ArrayList<>();

        if (!skillGaps.isEmpty()) {
            actions.add(
                    "Develop the identified missing skills"
            );
        }

        if (request.getProjects() == null
                || request.getProjects().isBlank()) {

            actions.add(
                    "Build practical projects related to the target career"
            );
        }

        if (request.getExperience() == null
                || request.getExperience().isBlank()) {

            actions.add(
                    "Gain practical experience through internships or projects"
            );
        }

        if (request.getEducation() == null
                || request.getEducation().isBlank()) {

            actions.add(
                    "Complete and maintain your academic profile"
            );
        }

        actions.add(
                "Keep your Skill DNA and project portfolio updated"
        );

        actions.add(
                "Track learning progress and review career goals regularly"
        );

        return actions;
    }
}