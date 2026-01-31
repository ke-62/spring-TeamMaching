package com.sejong.recruit.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class
User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;  // 이메일

    private String passwordHash;  // 비밀번호 해시

    @Column(nullable = false)
    private String fullName;  // 이름

    @Column(unique = true)
    private String studentId;  // 학번

    private String major;  // 학과

    @Column(columnDefinition = "TEXT")
    private String bio;  // 자기소개

    @Column(columnDefinition = "TEXT")
    private String techStack;  // 기술스택 (쉼표 구분)

    private String githubUrl;  // GitHub URL

    @Column(columnDefinition = "TEXT")
    private String collaborationKeywords;  // 협업 키워드 (AI 분석 결과)

    @Column(columnDefinition = "TEXT")
    private String aiSummary;  // AI 프로필 요약

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}