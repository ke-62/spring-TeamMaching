package com.sejong.recruit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 동료 평가 엔티티
 * 프로젝트 종료 후 팀원 간 평가를 저장
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 평가 대상자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private User reviewedUser;

    // 평가 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    // 어떤 프로젝트에 대한 평가인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_post_id", nullable = false)
    private RecruitPost recruitPost;

    // 협업 점수 (1-5)
    @Column(nullable = false)
    private Integer collaborationScore;

    // 기술 역량 점수 (1-5)
    @Column(nullable = false)
    private Integer technicalScore;

    // 책임감 점수 (1-5)
    @Column(nullable = false)
    private Integer responsibilityScore;

    // 자유 서술 평가 (AI 요약 대상)
    @Column(nullable = false, length = 2000)
    private String comment;

    // AI 요약 결과 (나중에 추가될 필드)
    @Column(length = 500)
    private String aiSummary;

    // 긍정 키워드 (AI 추출)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_positive_keywords", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "keyword")
    @Builder.Default
    private java.util.List<String> positiveKeywords = new java.util.ArrayList<>();

    // 개선 키워드 (AI 추출)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_improvement_keywords", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "keyword")
    @Builder.Default
    private java.util.List<String> improvementKeywords = new java.util.ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
