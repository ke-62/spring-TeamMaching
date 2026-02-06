package com.sejong.recruit.service;

import com.sejong.recruit.dto.PeerReviewDto;
import com.sejong.recruit.domain.review.entity.PeerReview;
import com.sejong.recruit.domain.project.entity.Project;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.repository.PeerReviewRepository;
import com.sejong.recruit.repository.ProjectRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeerReviewService {

    private final PeerReviewRepository peerReviewRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public PeerReviewDto.Response createReview(String reviewerStudentId, PeerReviewDto.CreateRequest request) {
        User reviewer = userRepository.findByStudentId(reviewerStudentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User reviewedUser = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(request.getRecruitPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (peerReviewRepository.existsByEvaluatorAndEvaluateeAndProjectId(reviewer, reviewedUser, project.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        PeerReview review = PeerReview.builder()
                .project(project)
                .evaluator(reviewer)
                .evaluatee(reviewedUser)
                .ratingCollaboration(request.getCollaborationScore())
                .ratingTechnical(request.getTechnicalScore())
                .reviewText(request.getComment())
                .build();

        review = peerReviewRepository.save(review);
        return PeerReviewDto.Response.from(review);
    }

    @Transactional(readOnly = true)
    public List<PeerReviewDto.Response> getReviewsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return peerReviewRepository.findByEvaluateeOrderByCreatedAtDesc(user).stream()
                .map(PeerReviewDto.Response::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PeerReviewDto.Response> getReviewsForProject(Long projectId) {
        return peerReviewRepository.findByProjectId(projectId).stream()
                .map(PeerReviewDto.Response::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PeerReviewDto.UserReviewSummary getUserReviewSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Double avgCollaboration = peerReviewRepository.getAverageCollaborationScore(userId);
        Double avgTechnical = peerReviewRepository.getAverageTechnicalScore(userId);
        Long totalCount = peerReviewRepository.countByEvaluatee(user);

        List<PeerReview> recentReviews = peerReviewRepository.findByEvaluateeOrderByCreatedAtDesc(user);
        List<String> recentComments = recentReviews.stream()
                .filter(r -> r.getReviewText() != null && !r.getReviewText().isBlank())
                .map(PeerReview::getReviewText)
                .limit(5)
                .toList();

        return PeerReviewDto.UserReviewSummary.builder()
                .userId(userId)
                .userName(user.getFullName())
                .totalReviewCount(totalCount != null ? totalCount : 0L)
                .averageCollaborationScore(avgCollaboration != null ? avgCollaboration : 0.0)
                .averageTechnicalScore(avgTechnical != null ? avgTechnical : 0.0)
                .averageResponsibilityScore(0.0)
                .topPositiveKeywords(List.of())
                .recentComments(recentComments)
                .build();
    }
}
