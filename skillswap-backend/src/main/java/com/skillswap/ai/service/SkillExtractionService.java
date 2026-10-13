package com.skillswap.ai.service;

import com.skillswap.ai.dto.SkillExtractionRequest;
import com.skillswap.ai.dto.SkillExtractionResponse;
import com.skillswap.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class SkillExtractionService {

    private static final List<String> KNOWN_SKILLS =
            List.of(
                    "java",
                    "spring boot",
                    "spring",
                    "javascript",
                    "typescript",
                    "react",
                    "angular",
                    "vue",
                    "html",
                    "css",
                    "python",
                    "django",
                    "flask",
                    "fastapi",
                    "c",
                    "c++",
                    "c#",
                    "kotlin",
                    "swift",
                    "php",
                    "node.js",
                    "nodejs",
                    "express",
                    "mysql",
                    "postgresql",
                    "mongodb",
                    "redis",
                    "sql",
                    "docker",
                    "kubernetes",
                    "aws",
                    "azure",
                    "google cloud",
                    "git",
                    "github",
                    "machine learning",
                    "deep learning",
                    "artificial intelligence",
                    "data science",
                    "tensorflow",
                    "pytorch",
                    "figma",
                    "ui/ux",
                    "ui ux",
                    "cyber security",
                    "cybersecurity",
                    "devops",
                    "communication",
                    "leadership",
                    "public speaking"
            );

    public SkillExtractionResponse extractSkills(
            SkillExtractionRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Skill extraction request is required"
            );
        }

        request.normalize();

        if (request.getText() == null
                || request.getText().isBlank()) {

            throw new BadRequestException(
                    "Text is required for skill extraction"
            );
        }

        String text =
                request.getText()
                        .toLowerCase(Locale.ROOT);

        List<String> extractedSkills =
                new ArrayList<>();

        for (String skill : KNOWN_SKILLS) {

            if (containsSkill(text, skill)) {

                String displaySkill =
                        toDisplayName(skill);

                if (!extractedSkills.contains(displaySkill)) {
                    extractedSkills.add(displaySkill);
                }
            }
        }

        SkillExtractionResponse response =
                SkillExtractionResponse.success(
                        extractedSkills
                );

        response.setSourceType(
                request.getSourceType()
        );

        response.setSourceId(
                request.getSourceId()
        );

        if (extractedSkills.isEmpty()) {
            response.setMessage(
                    "No recognized skills were found in the provided text"
            );
        }

        return response;
    }

    private boolean containsSkill(
            String text,
            String skill
    ) {
        if (text == null || skill == null) {
            return false;
        }

        String normalizedSkill =
                skill.toLowerCase(Locale.ROOT);

        if (text.contains(normalizedSkill)) {
            return true;
        }

        /*
         * Handle simple word-based matching for skills
         * containing punctuation or spacing variations.
         */
        String[] words =
                normalizedSkill
                        .replace("/", " ")
                        .replace("-", " ")
                        .split("\\s+");

        if (words.length == 0) {
            return false;
        }

        return Arrays.stream(words)
                .allMatch(text::contains);
    }

    private String toDisplayName(
            String skill
    ) {
        return switch (skill) {
            case "java" ->
                    "Java";

            case "spring boot" ->
                    "Spring Boot";

            case "spring" ->
                    "Spring";

            case "javascript" ->
                    "JavaScript";

            case "typescript" ->
                    "TypeScript";

            case "react" ->
                    "React";

            case "angular" ->
                    "Angular";

            case "vue" ->
                    "Vue";

            case "html" ->
                    "HTML";

            case "css" ->
                    "CSS";

            case "python" ->
                    "Python";

            case "django" ->
                    "Django";

            case "flask" ->
                    "Flask";

            case "fastapi" ->
                    "FastAPI";

            case "c" ->
                    "C";

            case "c++" ->
                    "C++";

            case "c#" ->
                    "C#";

            case "kotlin" ->
                    "Kotlin";

            case "swift" ->
                    "Swift";

            case "php" ->
                    "PHP";

            case "node.js", "nodejs" ->
                    "Node.js";

            case "express" ->
                    "Express";

            case "mysql" ->
                    "MySQL";

            case "postgresql" ->
                    "PostgreSQL";

            case "mongodb" ->
                    "MongoDB";

            case "redis" ->
                    "Redis";

            case "sql" ->
                    "SQL";

            case "docker" ->
                    "Docker";

            case "kubernetes" ->
                    "Kubernetes";

            case "aws" ->
                    "AWS";

            case "azure" ->
                    "Azure";

            case "google cloud" ->
                    "Google Cloud";

            case "git" ->
                    "Git";

            case "github" ->
                    "GitHub";

            case "machine learning" ->
                    "Machine Learning";

            case "deep learning" ->
                    "Deep Learning";

            case "artificial intelligence" ->
                    "Artificial Intelligence";

            case "data science" ->
                    "Data Science";

            case "tensorflow" ->
                    "TensorFlow";

            case "pytorch" ->
                    "PyTorch";

            case "figma" ->
                    "Figma";

            case "ui/ux", "ui ux" ->
                    "UI/UX";

            case "cyber security", "cybersecurity" ->
                    "Cyber Security";

            case "devops" ->
                    "DevOps";

            case "communication" ->
                    "Communication";

            case "leadership" ->
                    "Leadership";

            case "public speaking" ->
                    "Public Speaking";

            default ->
                    skill;
        };
    }
}