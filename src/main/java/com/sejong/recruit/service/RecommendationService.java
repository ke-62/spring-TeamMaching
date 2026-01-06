package com.sejong.recruit.service;

import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.entity.RecruitPost;
import com.sejong.recruit.entity.User;
import com.sejong.recruit.repository.RecruitPostRepository;
import com.sejong.recruit.repository.ReviewRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 기반 팀원 추천 서비스
 * 현재는 룰 기반 추천, 향후 AI 모델 연동 가능
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserRepository userRepository;
    private final RecruitPostRepository recruitPostRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 모집 공고에 적합한 팀원 추천
     */
    public List<RecommendedUserDto> recommendUsers(Long recruitPostId, int limit) {
        // 모집 공고 조회
        RecruitPost recruitPost = recruitPostRepository.findById(recruitPostId)
                .orElseThrow(() -> new RuntimeException("모집 공고를 찾을 수 없습니다"));

        // 모든 사용자 조회 (작성자 제외)
        List<User> allUsers = userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(recruitPost.getAuthor().getId()))
                .collect(Collectors.toList());

        // 각 사용자에 대해 점수 계산
        List<RecommendedUserDto> recommendations = allUsers.stream()
                .map(user -> calculateMatchScore(user, recruitPost))
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(limit)
                .collect(Collectors.toList());

        return recommendations;
    }

    /**
     * 사용자와 모집 공고 간의 매칭 점수 계산
     */
    private RecommendedUserDto calculateMatchScore(User user, RecruitPost recruitPost) {
        double score = 0.0;
        Map<String, String> reasons = new HashMap<>();

        // 1. 기술 스택 매칭 (40점)
        List<String> requiredTechStacks = recruitPost.getRequiredTechStacks();
        List<String> userTechStacks = user.getTechStacks();

        long matchingTechCount = requiredTechStacks.stream()
                .filter(userTechStacks::contains)
                .count();

        double techScore = requiredTechStacks.isEmpty() ? 0 :
                (matchingTechCount / (double) requiredTechStacks.size()) * 40;
        score += techScore;

        if (matchingTechCount > 0) {
            reasons.put("기술 스택", String.format("%.0f%% 일치 (%d/%d)",
                    techScore / 40 * 100, matchingTechCount, requiredTechStacks.size()));
        }

        // 2. 프로젝트 유형 관심도 (20점)
        List<String> userInterests = user.getInterests();
        String projectType = recruitPost.getProjectType().name().toLowerCase();

        if (userInterests.contains(projectType) || userInterests.contains("전체")) {
            score += 20;
            reasons.put("관심 분야", "프로젝트 유형에 관심 있음");
        }

        // 3. 평가 점수 (30점)
        Double avgCollaboration = reviewRepository.getAverageCollaborationScore(user.getId());
        Double avgTechnical = reviewRepository.getAverageTechnicalScore(user.getId());
        Double avgResponsibility = reviewRepository.getAverageResponsibilityScore(user.getId());

        if (avgCollaboration != null && avgTechnical != null && avgResponsibility != null) {
            double avgScore = (avgCollaboration + avgTechnical + avgResponsibility) / 3.0;
            double reviewScore = (avgScore / 5.0) * 30;
            score += reviewScore;
            reasons.put("동료 평가", String.format("평균 %.1f/5.0점", avgScore));
        }

        // 4. GitHub 활동 (10점) - 현재는 URL 유무만 체크, 향후 API 연동
        if (user.getGithubUrl() != null && !user.getGithubUrl().isEmpty()) {
            score += 10;
            reasons.put("GitHub", "프로필 등록됨");
        }

        // DTO 생성
        UserDto userDto = convertUserToDto(user);

        return RecommendedUserDto.builder()
                .user(userDto)
                .matchScore(Math.round(score * 10) / 10.0)  // 소수점 1자리
                .matchReasons(reasons)
                .build();
    }

    /**
     * User 엔티티를 UserDto로 변환
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

    /**
     * 추천 사용자 DTO
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class RecommendedUserDto {
        private UserDto user;
        private Double matchScore;  // 0-100 점수
        private Map<String, String> matchReasons;  // 추천 이유
    }
}
