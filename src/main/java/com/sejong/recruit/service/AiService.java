package com.sejong.recruit.service;

import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.domain.review.entity.PeerReview;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.repository.PeerReviewRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final UserRepository userRepository;
    private final PeerReviewRepository peerReviewRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.api.url:http://localhost:8000}")
    private String aiServerUrl;

    @Transactional
    public List<String> syncProfile(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<PeerReview> reviews = peerReviewRepository.findByEvaluateeId(user.getId());

        if (reviews.size() < 5) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    "리뷰 데이터가 부족합니다. (현재 " + reviews.size() + "개 / 최소 5개 필요)");
        }

        Map<String, Object> requestBody = buildRequestBody(user, reviews);
        List<String> hashtags = callAiServer(requestBody);

        user.setAiSummary(String.join(" ", hashtags));
        userRepository.save(user);

        return hashtags;
    }

    private Map<String, Object> buildRequestBody(User user, List<PeerReview> reviews) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", user.getId());
        body.put("userName", user.getFullName());
        body.put("department", user.getMajor());
        body.put("techStacks", parseTechStacks(user.getTechStack()));
        body.put("githubUrl", user.getGithubUrl());

        List<Map<String, Object>> reviewList = new ArrayList<>();
        double totalParticipation = 0, totalTechnical = 0, totalCommunication = 0;

        for (PeerReview review : reviews) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("projectTitle", review.getProject().getTitle());
            r.put("projectType", extractProjectType(review.getProject().getRequiredRoles()));

            int participation = review.getRatingCollaboration() != null ? review.getRatingCollaboration() : 0;
            int technical = review.getRatingTechnical() != null ? review.getRatingTechnical() : 0;
            int communication = review.getRatingCommunication() != null ? review.getRatingCommunication() : 0;

            r.put("participationScore", participation);
            r.put("technicalScore", technical);
            r.put("communicationScore", communication);
            r.put("comment", review.getReviewText());
            reviewList.add(r);

            totalParticipation += participation;
            totalTechnical += technical;
            totalCommunication += communication;
        }

        body.put("reviews", reviewList);
        body.put("totalProjects", (int) reviews.stream()
                .map(r -> r.getProject().getId())
                .distinct()
                .count());
        body.put("averageParticipation", totalParticipation / reviews.size());
        body.put("averageTechnical", totalTechnical / reviews.size());
        body.put("averageCommunication", totalCommunication / reviews.size());

        return body;
    }

    private List<String> parseTechStacks(String techStack) {
        if (techStack == null || techStack.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(techStack.split(","));
    }

    private String extractProjectType(String requiredRoles) {
        if (requiredRoles == null || requiredRoles.isBlank()) return "other";
        for (String part : requiredRoles.split("\\|")) {
            if (part.startsWith("projectType:")) {
                return part.substring("projectType:".length());
            }
        }
        return "other";
    }

    @SuppressWarnings("unchecked")
    private List<String> callAiServer(Map<String, Object> requestBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    aiServerUrl + "/analyze-profile",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body != null && Boolean.TRUE.equals(body.get("success"))) {
                Object hashtagsObj = body.get("hashtags");
                if (hashtagsObj instanceof List) {
                    return (List<String>) hashtagsObj;
                }
                return Collections.emptyList();
            } else {
                String error = body != null ? (String) body.get("error") : "Unknown error";
                log.error("AI 서버 응답 실패: {}", error);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, error);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 서버 호출 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "AI 서버 호출 실패: " + e.getMessage());
        }
    }
}
