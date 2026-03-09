package com.sejong.recruit.common.controller;

import com.sejong.recruit.dto.PeerReviewDto;
import com.sejong.recruit.service.PeerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class PeerReviewController {

    private final PeerReviewService peerReviewService;

    @PostMapping
    public PeerReviewDto.Response createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PeerReviewDto.CreateRequest request) {
        return peerReviewService.createReview(userDetails.getUsername(), request);
    }

    @GetMapping("/user/{userId}")
    public List<PeerReviewDto.Response> getReviewsForUser(@PathVariable Long userId) {
        return peerReviewService.getReviewsForUser(userId);
    }

    @GetMapping("/project/{projectId}")
    public List<PeerReviewDto.Response> getReviewsForProject(@PathVariable Long projectId) {
        return peerReviewService.getReviewsForProject(projectId);
    }

    @GetMapping("/user/{userId}/summary")
    public PeerReviewDto.UserReviewSummary getUserReviewSummary(@PathVariable Long userId) {
        return peerReviewService.getUserReviewSummary(userId);
    }
}
