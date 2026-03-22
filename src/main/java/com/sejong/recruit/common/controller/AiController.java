package com.sejong.recruit.common.controller;

import com.sejong.recruit.common.response.ApiResponse;
import com.sejong.recruit.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/sync")
    public ApiResponse<Map<String, Object>> syncProfile(@AuthenticationPrincipal UserDetails userDetails) {
        List<String> hashtags = aiService.syncProfile(userDetails.getUsername());
        return ApiResponse.success(Map.of(
                "hashtags", hashtags,
                "aiSummary", String.join(" ", hashtags)
        ));
    }
}
