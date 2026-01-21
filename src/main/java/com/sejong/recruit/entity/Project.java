package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import com.sejong.recruit.entity.User;
import com.sejong.recruit.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // [추가] 프로젝트 멤버와의 양방향 연관관계
    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    public void changeStatus(ProjectStatus status) {
        this.status = status;
    }

    // 멤버 추가 편의 메서드
    public void addMember(ProjectMember member) {
        this.members.add(member);
    }
}