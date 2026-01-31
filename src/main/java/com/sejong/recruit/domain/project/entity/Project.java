package com.sejong.recruit.domain.project.entity;

import com.sejong.recruit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 프로젝트 모집 공고 엔티티
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;  // 프로젝트 리더 (작성자)

    @Column(nullable = false)
    private String title;  // 프로젝트 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;  // 프로젝트 상세 설명

    @Column(nullable = false)
    private String requiredRoles;  // 필요한 역할 (예: "Backend,Frontend,Designer")

    private LocalDateTime deadline;  // 모집 마감일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.RECRUITING;  // RECRUITING, IN_PROGRESS, COMPLETED

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}