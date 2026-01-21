package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import com.sejong.recruit.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 프로젝트 모집 공고 엔티티
 */
@Entity
@Getter
@Table(name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String requiredRoles;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    public void changeStatus(ProjectStatus status) {
        this.status = status;
    }
}