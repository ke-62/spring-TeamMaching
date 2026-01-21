package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import com.sejong.recruit.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * 프로젝트 지원 내역 엔티티
 */
@Entity
@Getter
@Table(name = "applications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Application extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }
}