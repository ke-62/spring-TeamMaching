package com.sejong.recruit.controller;

import com.sejong.recruit.dto.ReviewDto;
import com.sejong.recruit.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 동료 평가 API 컨트롤러
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 동료 평가 작성
     */
    @PostMapping
    public ResponseEntity<ReviewDto.Response> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewDto.CreateRequest request
    ) {
        ReviewDto.Response response = reviewService.createReview(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 사용자가 받은 평가 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto.Response>> getReviewsForUser(@PathVariable Long userId) {
        List<ReviewDto.Response> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 프로젝트의 평가 목록 조회
     */
    @GetMapping("/project/{recruitPostId}")
    public ResponseEntity<List<ReviewDto.Response>> getReviewsForProject(@PathVariable Long recruitPostId) {
        List<ReviewDto.Response> reviews = reviewService.getReviewsForProject(recruitPostId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 사용자 평가 요약 정보 조회 (프로필용)
     */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<ReviewDto.UserReviewSummary> getUserReviewSummary(@PathVariable Long userId) {
        ReviewDto.UserReviewSummary summary = reviewService.getUserReviewSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
