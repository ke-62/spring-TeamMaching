package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 프로젝트에 최종 참여하게 된 멤버 정보 엔티티
 * AI 평가 시 프로젝트 참여 여부를 판단하는 기준이 됩니다.
 */
@Entity
@Getter
@Table(name = "project_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProjectMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role; // 담당 역할 (예: FE, BE, AI, PM 등)

    // 역할 변경 시 사용
    public void changeRole(String role) {
        this.role = role;
    }
}