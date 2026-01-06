package com.sejong.recruit.service;

import com.sejong.recruit.dto.ReviewDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.entity.RecruitPost;
import com.sejong.recruit.entity.Review;
import com.sejong.recruit.entity.User;
import com.sejong.recruit.repository.RecruitPostRepository;
import com.sejong.recruit.repository.ReviewRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RecruitPostRepository recruitPostRepository;

    /**
     * 동료 평가 작성
     */
    @Transactional
    public ReviewDto.Response createReview(String reviewerStudentId, ReviewDto.CreateRequest request) {
        // 평가 작성자 조회
        User reviewer = userRepository.findByStudentId(reviewerStudentId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 평가 대상자 조회
        User reviewedUser = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new RuntimeException("평가 대상자를 찾을 수 없습니다"));

        // 프로젝트 조회
        RecruitPost recruitPost = recruitPostRepository.findById(request.getRecruitPostId())
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다"));

        // 자기 자신을 평가하는지 확인
        if (reviewer.getId().equals(reviewedUser.getId())) {
            throw new RuntimeException("자기 자신은 평가할 수 없습니다");
        }

        // 중복 평가 확인
        boolean alreadyReviewed = reviewRepository.existsByReviewerAndReviewedUserAndRecruitPost(
                reviewer.getId(),
                reviewedUser.getId(),
                recruitPost.getId()
        );
        if (alreadyReviewed) {
            throw new RuntimeException("이미 해당 사용자에 대한 평가를 작성했습니다");
        }

        // Review 엔티티 생성
        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewedUser(reviewedUser)
                .recruitPost(recruitPost)
                .collaborationScore(request.getCollaborationScore())
                .technicalScore(request.getTechnicalScore())
                .responsibilityScore(request.getResponsibilityScore())
                .comment(request.getComment())
                .build();

        // TODO: AI 요약 및 키워드 추출 (나중에 구현)
        // review.setAiSummary(aiService.summarize(request.getComment()));
        // review.setPositiveKeywords(aiService.extractPositiveKeywords(request.getComment()));

        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    /**
     * 특정 사용자가 받은 평가 목록 조회
     */
    public List<ReviewDto.Response> getReviewsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<Review> reviews = reviewRepository.findByReviewedUserOrderByCreatedAtDesc(user);

        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 프로젝트의 평가 목록 조회
     */
    public List<ReviewDto.Response> getReviewsForProject(Long recruitPostId) {
        return reviewRepository.findByRecruitPostId(recruitPostId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 평가 요약 정보 조회 (프로필용)
     */
    public ReviewDto.UserReviewSummary getUserReviewSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<Review> reviews = reviewRepository.findByReviewedUserOrderByCreatedAtDesc(user);

        // 평균 점수 계산
        Double avgCollaboration = reviewRepository.getAverageCollaborationScore(userId);
        Double avgTechnical = reviewRepository.getAverageTechnicalScore(userId);
        Double avgResponsibility = reviewRepository.getAverageResponsibilityScore(userId);

        // 최근 코멘트 3개
        List<String> recentComments = reviews.stream()
                .limit(3)
                .map(Review::getComment)
                .collect(Collectors.toList());

        // TODO: 긍정 키워드 집계 (나중에 AI 구현 후)
        List<String> topKeywords = reviews.stream()
                .flatMap(r -> r.getPositiveKeywords().stream())
                .distinct()
                .limit(3)
                .collect(Collectors.toList());

        return ReviewDto.UserReviewSummary.builder()
                .userId(userId)
                .userName(user.getName())
                .totalReviewCount(reviews.size())
                .averageCollaborationScore(avgCollaboration != null ? avgCollaboration : 0.0)
                .averageTechnicalScore(avgTechnical != null ? avgTechnical : 0.0)
                .averageResponsibilityScore(avgResponsibility != null ? avgResponsibility : 0.0)
                .topPositiveKeywords(topKeywords)
                .recentComments(recentComments)
                .build();
    }

    /**
     * Review 엔티티를 Response DTO로 변환
     */
    private ReviewDto.Response convertToResponse(Review review) {
        return ReviewDto.Response.builder()
                .id(review.getId())
                .reviewedUser(convertUserToDto(review.getReviewedUser()))
                .reviewer(convertUserToDto(review.getReviewer()))
                .recruitPostId(review.getRecruitPost().getId())
                .recruitPostTitle(review.getRecruitPost().getTitle())
                .collaborationScore(review.getCollaborationScore())
                .technicalScore(review.getTechnicalScore())
                .responsibilityScore(review.getResponsibilityScore())
                .comment(review.getComment())
                .aiSummary(review.getAiSummary())
                .positiveKeywords(review.getPositiveKeywords())
                .improvementKeywords(review.getImprovementKeywords())
                .createdAt(review.getCreatedAt())
                .build();
    }

    /**
     * User 엔티티를 UserDto로 변환 (간단한 정보만)
     */
    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setStudentId(user.getStudentId());
        dto.setName(user.getName());
        dto.setDepartment(user.getDepartment());
        dto.setGrade(user.getGrade());
        dto.setEmail(user.getEmail());
        dto.setTechStacks(user.getTechStacks());
        dto.setInterests(user.getInterests());
        dto.setGithubUrl(user.getGithubUrl());
        dto.setProfileImage(user.getProfileImage());
        dto.setBio(user.getBio());
        return dto;
    }
}
