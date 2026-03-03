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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String studentId;

    private String university;

    @Column(name = "department")
    private String major;

    @Builder.Default
    private Boolean isAuthenticated = false;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String techStack;

    private String githubUrl;

    @Column(columnDefinition = "TEXT")
    private String collaborationKeywords;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
