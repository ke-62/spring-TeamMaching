package com.sejong.recruit.entity;

import com.sejong.recruit.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 동료 평가 엔티티
 */
@Entity
@Getter
@Table(name = "peer_reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PeerReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluatee_id", nullable = false)
    private User evaluatee;

    private Integer ratingCollaboration;
    private Integer ratingTechnical;

    @Column(columnDefinition = "TEXT")
    private String reviewText;
}