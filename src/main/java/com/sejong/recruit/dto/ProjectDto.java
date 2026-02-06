package com.sejong.recruit.dto;

import com.sejong.recruit.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProjectDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String description;
        private String projectType;
        private List<String> requiredTechStacks;
        private Integer recruitNumber;
        private String deadline;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String description;
        private String projectType;
        private List<String> requiredTechStacks;
        private Integer recruitNumber;
        private String deadline;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String projectType;
        private List<String> requiredTechStacks;
        private Integer recruitNumber;
        private String deadline;
        private Long authorId;
        private UserDto author;
        private String status;
        private String createdAt;
        private String updatedAt;

        public static Response from(Project project) {
            String content = project.getContent();
            String requiredRoles = project.getRequiredRoles();

            String projectType = extractField(requiredRoles, "projectType", "other");
            int recruitNumber = Integer.parseInt(extractField(requiredRoles, "recruitNumber", "1"));
            List<String> techStacks = extractTechStacks(requiredRoles);

            return Response.builder()
                    .id(project.getId())
                    .title(project.getTitle())
                    .description(content)
                    .projectType(projectType)
                    .requiredTechStacks(techStacks)
                    .recruitNumber(recruitNumber)
                    .deadline(project.getDeadline() != null ? project.getDeadline().toString() : null)
                    .authorId(project.getLeader().getId())
                    .author(UserDto.from(project.getLeader()))
                    .status(project.getStatus().name())
                    .createdAt(project.getCreatedAt() != null ? project.getCreatedAt().toString() : null)
                    .updatedAt(project.getUpdatedAt() != null ? project.getUpdatedAt().toString() : null)
                    .build();
        }

        private static String extractField(String roles, String key, String defaultValue) {
            if (roles == null || roles.isBlank()) return defaultValue;
            for (String part : roles.split("\\|")) {
                if (part.startsWith(key + ":")) {
                    return part.substring(key.length() + 1);
                }
            }
            return defaultValue;
        }

        private static List<String> extractTechStacks(String roles) {
            if (roles == null || roles.isBlank()) return Collections.emptyList();
            for (String part : roles.split("\\|")) {
                if (part.startsWith("techStacks:")) {
                    String csv = part.substring("techStacks:".length());
                    return Arrays.stream(csv.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toList();
                }
            }
            return Collections.emptyList();
        }
    }
}
