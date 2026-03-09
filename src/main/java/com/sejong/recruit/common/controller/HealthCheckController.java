package com.sejong.recruit.common.controller;

import com.sejong.recruit.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서버 가동 상태를 확인하기 위한 최소 기능 컨트롤러
 */
@RestController
public class HealthCheckController {

    /**
     * 서버 상태 확인 API
     * GET http://localhost:8080/api/health
     */
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Sejong Recruit Foundation is ready.");
    }
}