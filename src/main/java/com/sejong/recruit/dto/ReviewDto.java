package com.sejong.recruit.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDto {

    /**
     * 동료 평가 작성 요청
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotNull(message = "평가 대상자 ID는 필수입니다")
        private Long reviewedUserId;

        @NotNull(message = "프로젝트 ID는 필수입니다")
        private Long recruitPostId;

        @NotNull(message = "협업 점수는 필수입니다")
        @Min(value = 1, message = "협업 점수는 1 이상이어야 합니다")
        @Max(value = 5, message = "협업 점수는 5 이하여야 합니다")
        private Integer collaborationScore;

        @NotNull(message = "기술 역량 점수는 필수입니다")
        @Min(value = 1, message = "기술 역량 점수는 1 이상이어야 합니다")
        @Max(value = 5, message = "기술 역량 점수는 5 이하여야 합니다")
        private Integer technicalScore;

        @NotNull(message = "책임감 점수는 필수입니다")
        @Min(value = 1, message = "책임감 점수는 1 이상이어야 합니다")
        @Max(value = 5, message = "책임감 점수는 5 이하여야 합니다")
        private Integer responsibilityScore;

        @NotBlank(message = "평가 내용은 필수입니다")
        @Size(max = 2000, message = "평가 내용은 2000자를 초과할 수 없습니다")
        private String comment;
    }

    /**
     * 동료 평가 응답
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private UserDto reviewedUser;  // 평가 대상자
        private UserDto reviewer;      // 평가 작성자
        private Long recruitPostId;
        private String recruitPostTitle;
        private Integer collaborationScore;
        private Integer technicalScore;
        private Integer responsibilityScore;
        private String comment;
        private String aiSummary;
        private List<String> positiveKeywords;
        private List<String> improvementKeywords;
        private LocalDateTime createdAt;
    }

    /**
     * 사용자별 평가 요약 (프로필에 표시용)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserReviewSummary {
        private Long userId;
        private String userName;
        private Integer totalReviewCount;
        private Double averageCollaborationScore;
        private Double averageTechnicalScore;
        private Double averageResponsibilityScore;
        private List<String> topPositiveKeywords;  // 상위 3개
        private List<String> recentComments;  // 최근 3개 평가 내용
    }
}
