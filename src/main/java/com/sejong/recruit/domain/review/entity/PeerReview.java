package com.sejong.recruit.domain.review.entity;

import com.sejong.recruit.domain.project.entity.Project;
import com.sejong.recruit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 동료 평가 엔티티
 */
@Entity
@Table(name = "peer_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;  // 평가자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluatee_id", nullable = false)
    private User evaluatee;  // 피평가자

    @Column(nullable = false)
    private Integer ratingCollaboration;  // 협업 능력 평점 (1-5)

    @Column(nullable = false)
    private Integer ratingTechnical;  // 기술 능력 평점 (1-5)

    @Column(columnDefinition = "TEXT")
    private String reviewText;  // 평가 상세 내용

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}