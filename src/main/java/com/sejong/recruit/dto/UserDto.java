package com.sejong.recruit.dto;

import com.sejong.recruit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String studentId;
    private String name;
    private String department;
    private Integer grade;
    private String email;
    private String phoneNumber;
    private List<String> techStacks;
    private List<String> interests;
    private String githubUrl;
    private String profileImage;
    private String bio;
    private LocalDateTime createdAt;
    
    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .name(user.getName())
                .department(user.getDepartment())
                .grade(user.getGrade())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .techStacks(user.getTechStacks())
                .interests(user.getInterests())
                .githubUrl(user.getGithubUrl())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
