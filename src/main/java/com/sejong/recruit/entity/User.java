package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 정보 엔티티
 */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    private String studentId;
    private String major;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String techStack;

    private String githubUrl;

    @Column(columnDefinition = "TEXT")
    private String collaborationKeywords;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    public void updateProfile(String bio, String techStack, String githubUrl) {
        this.bio = bio;
        this.techStack = techStack;
        this.githubUrl = githubUrl;
    }
}