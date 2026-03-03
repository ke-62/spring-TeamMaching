package com.sejong.recruit.dto;

import com.sejong.recruit.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private String studentId;
    private String department;
    private List<String> techStacks;
    private List<String> interests;
    private String githubUrl;
    private String bio;
    private String createdAt;

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getFullName())
                .studentId(user.getStudentId())
                .department(user.getMajor())
                .techStacks(parseCsv(user.getTechStack()))
                .interests(parseCsv(user.getCollaborationKeywords()))
                .githubUrl(user.getGithubUrl())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }

    private static List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptyList();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
