package com.skillswap.ai.service;

import com.skillswap.ai.dto.LearningRecommendationRequest;
import com.skillswap.ai.dto.LearningRecommendationResponse;
import com.skillswap.common.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class LearningRecommendationService {

    public LearningRecommendationResponse recommend(
            LearningRecommendationRequest request
    ) {
        if (request == null) {
            throw new BadRequestException(
                    "Learning recommendation request is required"
            );
        }

        request.normalize();

        if (request.getLearningGoal() == null
                || request.getLearningGoal().isBlank()) {
            throw new BadRequestException(
                    "Learning goal is required"
            );
        }

        String goal =
                request.getLearningGoal()
                        .toLowerCase(Locale.ROOT);

        List<String> recommendedSkills =
                new ArrayList<>();

        List<String> recommendedTopics =
                new ArrayList<>();

        List<String> recommendedResources =
                new ArrayList<>();

        List<String> recommendedSteps =
                new ArrayList<>();

        buildRecommendations(
                goal,
                recommendedSkills,
                recommendedTopics,
                recommendedResources,
                recommendedSteps
        );

        return LearningRecommendationResponse.success(
                request.getLearningGoal(),
                recommendedSkills,
                recommendedTopics,
                recommendedResources,
                recommendedSteps
        );
    }

    private void buildRecommendations(
            String goal,
            List<String> skills,
            List<String> topics,
            List<String> resources,
            List<String> steps
    ) {
        if (goal.contains("full stack")
                || goal.contains("fullstack")) {

            skills.add("HTML");
            skills.add("CSS");
            skills.add("JavaScript");
            skills.add("React");
            skills.add("Java");
            skills.add("Spring Boot");
            skills.add("MySQL");
            skills.add("Git");
            skills.add("Docker");

            topics.add("Frontend Development");
            topics.add("REST API Development");
            topics.add("Database Design");
            topics.add("Authentication");
            topics.add("Deployment");

            resources.add("Official documentation");
            resources.add("Hands-on coding projects");
            resources.add("API development exercises");

            steps.add("Learn HTML and CSS fundamentals");
            steps.add("Learn JavaScript");
            steps.add("Build React applications");
            steps.add("Learn Spring Boot REST APIs");
            steps.add("Connect React with Spring Boot");
            steps.add("Build a full-stack project");
            steps.add("Learn Docker and deployment");

            return;
        }

        if (goal.contains("react")
                || goal.contains("frontend")
                || goal.contains("front end")) {

            skills.add("HTML");
            skills.add("CSS");
            skills.add("JavaScript");
            skills.add("React");
            skills.add("TypeScript");
            skills.add("Git");

            topics.add("Components");
            topics.add("Props and State");
            topics.add("Hooks");
            topics.add("Routing");
            topics.add("API Integration");

            resources.add("React documentation");
            resources.add("Frontend projects");
            resources.add("REST API practice");

            steps.add("Learn JavaScript fundamentals");
            steps.add("Learn React components");
            steps.add("Practice state management");
            steps.add("Build multiple small projects");
            steps.add("Integrate REST APIs");

            return;
        }

        if (goal.contains("java")
                || goal.contains("spring")
                || goal.contains("backend")
                || goal.contains("back end")) {

            skills.add("Java");
            skills.add("Spring Boot");
            skills.add("Spring Security");
            skills.add("REST API");
            skills.add("SQL");
            skills.add("MySQL");
            skills.add("Git");
            skills.add("Docker");

            topics.add("Object-Oriented Programming");
            topics.add("Spring Boot");
            topics.add("REST APIs");
            topics.add("JPA");
            topics.add("Authentication");
            topics.add("Database Design");

            resources.add("Java documentation");
            resources.add("Spring documentation");
            resources.add("Backend development projects");

            steps.add("Learn Java fundamentals");
            steps.add("Learn OOP");
            steps.add("Learn Spring Boot");
            steps.add("Build REST APIs");
            steps.add("Learn JPA and MySQL");
            steps.add("Implement authentication");
            steps.add("Build a complete backend project");

            return;
        }

        if (goal.contains("python")
                || goal.contains("data science")
                || goal.contains("machine learning")
                || goal.contains("artificial intelligence")
                || goal.equals("ai")) {

            skills.add("Python");
            skills.add("SQL");
            skills.add("Data Science");
            skills.add("Machine Learning");

            topics.add("Python Programming");
            topics.add("Data Analysis");
            topics.add("Statistics");
            topics.add("Machine Learning");
            topics.add("Model Evaluation");

            resources.add("Python documentation");
            resources.add("Data analysis exercises");
            resources.add("Machine learning projects");

            steps.add("Learn Python");
            steps.add("Learn NumPy and Pandas");
            steps.add("Learn statistics");
            steps.add("Study machine learning fundamentals");
            steps.add("Build practical ML projects");

            return;
        }

        skills.add("Communication");
        skills.add("Problem Solving");
        skills.add("Programming Fundamentals");
        skills.add("Git");

        topics.add("Fundamentals");
        topics.add("Problem Solving");
        topics.add("Practical Projects");
        topics.add("Version Control");

        resources.add("Official documentation");
        resources.add("Practice exercises");
        resources.add("Hands-on projects");

        steps.add("Define the learning objective");
        steps.add("Learn the fundamentals");
        steps.add("Practice with small exercises");
        steps.add("Build a practical project");
        steps.add("Review and improve the project");
    }
}