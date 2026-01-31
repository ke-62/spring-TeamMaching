package com.sejong.recruit.domain.auth.controller;

import com.sejong.recruit.domain.auth.dto.SejongAuthDto;
import com.sejong.recruit.domain.auth.service.SejongPortalAuthService;
import com.sejong.recruit.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API", description = "세종대학교 포털 연동 인증")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SejongPortalAuthService sejongPortalAuthService;

    @Operation(summary = "세종 포털 로그인 인증", description = "학번과 비번을 통해 학생 정보를 가져옵니다.")
    @PostMapping("/login")
    public ApiResponse<SejongAuthDto.AuthResponse> login(@RequestBody SejongAuthDto.LoginRequest request) {
        SejongAuthDto.AuthResponse response = sejongPortalAuthService.authenticate(
                request.getStudentId(),
                request.getPassword()
        );
        return ApiResponse.success(response);
    }
}
