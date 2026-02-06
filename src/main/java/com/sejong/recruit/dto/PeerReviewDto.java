package com.sejong.recruit.dto;

import com.sejong.recruit.domain.review.entity.PeerReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PeerReviewDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long reviewedUserId;
        private Long recruitPostId;
        private Integer collaborationScore;
        private Integer technicalScore;
        private String comment;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private UserDto reviewedUser;
        private UserDto reviewer;
        private Long recruitPostId;
        private String recruitPostTitle;
        private Integer collaborationScore;
        private Integer technicalScore;
        private String comment;
        private String createdAt;

        public static Response from(PeerReview review) {
            return Response.builder()
                    .id(review.getId())
                    .reviewedUser(UserDto.from(review.getEvaluatee()))
                    .reviewer(UserDto.from(review.getEvaluator()))
                    .recruitPostId(review.getProject().getId())
                    .recruitPostTitle(review.getProject().getTitle())
                    .collaborationScore(review.getRatingCollaboration())
                    .technicalScore(review.getRatingTechnical())
                    .comment(review.getReviewText())
                    .createdAt(review.getCreatedAt() != null ? review.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserReviewSummary {
        private Long userId;
        private String userName;
        private Long totalReviewCount;
        private Double averageCollaborationScore;
        private Double averageTechnicalScore;
        private Double averageResponsibilityScore;
        private List<String> topPositiveKeywords;
        private List<String> recentComments;
    }
}
