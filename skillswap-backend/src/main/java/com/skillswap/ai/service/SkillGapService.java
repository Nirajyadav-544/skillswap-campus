package com.skillswap.ai.service;

import com.skillswap.ai.dto.SkillGapRequest;
import com.skillswap.ai.dto.SkillGapResponse;
import com.skillswap.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class SkillGapService {

    public SkillGapResponse analyze(
            SkillGapRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Skill gap request is required"
            );
        }

        request.normalize();

        if (request.getTargetCareer() == null
                || request.getTargetCareer().isBlank()) {
            throw new BadRequestException(
                    "Target career is required"
            );
        }

        List<String> currentSkills =
                parseSkills(request.getCurrentSkills());

        List<String> requiredSkills =
                getRequiredSkills(
                        request.getTargetCareer()
                );

        List<String> missingSkills =
                new ArrayList<>();

        for (String requiredSkill : requiredSkills) {
            if (!containsSkill(
                    currentSkills,
                    requiredSkill
            )) {
                missingSkills.add(requiredSkill);
            }
        }

        List<String> recommendedSkills =
                new ArrayList<>(missingSkills);

        int matchPercentage;

        if (requiredSkills.isEmpty()) {
            matchPercentage = 0;
        } else {
            int matched =
                    requiredSkills.size()
                            - missingSkills.size();

            matchPercentage =
                    (int) Math.round(
                            matched * 100.0
                                    / requiredSkills.size()
                    );
        }

        return SkillGapResponse.success(
                request.getTargetCareer(),
                currentSkills,
                missingSkills,
                recommendedSkills,
                matchPercentage
        );
    }

    private List<String> parseSkills(
            String skills
    ) {
        if (skills == null || skills.isBlank()) {
            return new ArrayList<>();
        }

        Set<String> normalized =
                new LinkedHashSet<>();

        Arrays.stream(
                        skills.split("[,;\\n]")
                )
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .forEach(value ->
                        normalized.add(
                                normalizeSkill(value)
                        )
                );

        return new ArrayList<>(normalized);
    }

    private List<String> getRequiredSkills(
            String targetCareer
    ) {
        String career =
                targetCareer
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (career.contains("full stack")
                || career.contains("fullstack")) {

            return List.of(
                    "HTML",
                    "CSS",
                    "JavaScript",
                    "React",
                    "Java",
                    "Spring Boot",
                    "SQL",
                    "Git",
                    "Docker"
            );
        }

        if (career.contains("backend")
                || career.contains("back end")) {

            return List.of(
                    "Java",
                    "Spring Boot",
                    "SQL",
                    "REST API",
                    "Git",
                    "Docker"
            );
        }

        if (career.contains("frontend")
                || career.contains("front end")) {

            return List.of(
                    "HTML",
                    "CSS",
                    "JavaScript",
                    "React",
                    "TypeScript",
                    "Git"
            );
        }

        if (career.contains("data scientist")
                || career.contains("data science")) {

            return List.of(
                    "Python",
                    "SQL",
                    "Data Science",
                    "Statistics",
                    "Machine Learning",
                    "Pandas"
            );
        }

        if (career.contains("machine learning")
                || career.contains("ml engineer")) {

            return List.of(
                    "Python",
                    "Machine Learning",
                    "Deep Learning",
                    "TensorFlow",
                    "PyTorch",
                    "SQL"
            );
        }

        if (career.contains("ai")
                || career.contains("artificial intelligence")) {

            return List.of(
                    "Python",
                    "Artificial Intelligence",
                    "Machine Learning",
                    "Deep Learning",
                    "Data Science"
            );
        }

        if (career.contains("devops")
                || career.contains("cloud")) {

            return List.of(
                    "Linux",
                    "Git",
                    "Docker",
                    "Kubernetes",
                    "AWS",
                    "CI/CD"
            );
        }

        if (career.contains("cyber")
                || career.contains("security")) {

            return List.of(
                    "Networking",
                    "Linux",
                    "Cyber Security",
                    "Ethical Hacking",
                    "Cryptography"
            );
        }

        if (career.contains("mobile")
                || career.contains("android")) {

            return List.of(
                    "Kotlin",
                    "Android",
                    "Java",
                    "Git",
                    "REST API"
            );
        }

        return List.of(
                "Communication",
                "Problem Solving",
                "Git",
                "Technical Knowledge",
                "Project Experience"
        );
    }

    private boolean containsSkill(
            List<String> currentSkills,
            String requiredSkill
    ) {
        String normalizedRequired =
                normalizeSkill(requiredSkill);

        return currentSkills.stream()
                .anyMatch(current ->
                        normalizeSkill(current)
                                .equals(normalizedRequired)
                                || normalizeSkill(current)
                                .contains(normalizedRequired)
                                || normalizedRequired
                                .contains(normalizeSkill(current))
                );
    }

    private String normalizeSkill(
            String skill
    ) {
        if (skill == null) {
            return "";
        }

        return skill
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}