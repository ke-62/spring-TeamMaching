package com.sejong.recruit.service;

import com.sejong.recruit.dto.ProjectDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.repository.PeerReviewRepository;
import com.sejong.recruit.repository.ProjectRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PeerReviewRepository peerReviewRepository;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public List<RecommendedUserDto> recommendUsers(Long projectId, int limit) {
        ProjectDto.Response project = projectService.getProject(projectId);

        List<String> requiredTechStacks = project.getRequiredTechStacks();
        Long leaderId = project.getAuthorId();

        List<User> allUsers = userRepository.findAll();

        List<RecommendedUserDto> scored = allUsers.stream()
                .filter(u -> !u.getId().equals(leaderId))
                .map(u -> {
                    double score = calculateScore(u, requiredTechStacks);
                    Map<String, String> reasons = calculateReasons(u, requiredTechStacks);
                    return new RecommendedUserDto(UserDto.from(u), score, reasons);
                })
                .sorted(Comparator.comparingDouble(RecommendedUserDto::getMatchScore).reversed())
                .limit(limit)
                .toList();

        return scored;
    }

    private double calculateScore(User user, List<String> requiredTechStacks) {
        double score = 0;

        // 기술 스택 매칭 (40점)
        if (user.getTechStack() != null && requiredTechStacks != null) {
            Set<String> userTechs = Arrays.stream(user.getTechStack().split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            long matchCount = requiredTechStacks.stream()
                    .filter(t -> userTechs.contains(t.toLowerCase()))
                    .count();
            if (!requiredTechStacks.isEmpty()) {
                score += (matchCount * 40.0) / requiredTechStacks.size();
            }
        }

        // 리뷰 점수 (30점)
        Double avgCollab = peerReviewRepository.getAverageCollaborationScore(user.getId());
        Double avgTech = peerReviewRepository.getAverageTechnicalScore(user.getId());
        if (avgCollab != null && avgTech != null) {
            double avgReview = (avgCollab + avgTech) / 2.0;
            score += (avgReview / 5.0) * 30;
        }

        // GitHub 프로필 유무 (10점)
        if (user.getGithubUrl() != null && !user.getGithubUrl().isBlank()) {
            score += 10;
        }

        // 프로필 완성도 (20점)
        int completeness = 0;
        if (user.getBio() != null && !user.getBio().isBlank()) completeness += 5;
        if (user.getTechStack() != null && !user.getTechStack().isBlank()) completeness += 5;
        if (user.getMajor() != null && !user.getMajor().isBlank()) completeness += 5;
        if (user.getCollaborationKeywords() != null && !user.getCollaborationKeywords().isBlank()) completeness += 5;
        score += completeness;

        return Math.min(score, 100);
    }

    private Map<String, String> calculateReasons(User user, List<String> requiredTechStacks) {
        Map<String, String> reasons = new LinkedHashMap<>();

        if (user.getTechStack() != null && requiredTechStacks != null && !requiredTechStacks.isEmpty()) {
            Set<String> userTechs = Arrays.stream(user.getTechStack().split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            long matchCount = requiredTechStacks.stream()
                    .filter(t -> userTechs.contains(t.toLowerCase()))
                    .count();
            reasons.put("기술 스택", matchCount + "/" + requiredTechStacks.size() + " 일치");
        }

        Double avgCollab = peerReviewRepository.getAverageCollaborationScore(user.getId());
        if (avgCollab != null) {
            reasons.put("동료 평가", String.format("평균 %.1f점", avgCollab));
        }

        if (user.getGithubUrl() != null && !user.getGithubUrl().isBlank()) {
            reasons.put("GitHub", "연동됨");
        }

        return reasons;
    }

    @Getter
    @AllArgsConstructor
    public static class RecommendedUserDto {
        private UserDto user;
        private double matchScore;
        private Map<String, String> matchReasons;
    }
}
