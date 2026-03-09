package com.sejong.recruit.common.controller;

import com.sejong.recruit.dto.AuthDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.common.response.ApiResponse;
import com.sejong.recruit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthDto.LoginResponse login(@RequestBody AuthDto.LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthDto.TokenResponse refresh(@RequestBody AuthDto.RefreshRequest request) {
        return authService.refreshToken(request);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return authService.getCurrentUser(userDetails.getUsername());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }
}
