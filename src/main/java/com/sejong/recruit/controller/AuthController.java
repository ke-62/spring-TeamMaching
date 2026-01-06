package com.sejong.recruit.controller;

import com.sejong.recruit.dto.AuthDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 로그인 (세종대 학사정보시스템 연동)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.TokenResponse> refreshToken(@Valid @RequestBody AuthDto.RefreshRequest request) {
        AuthDto.TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
    
    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT는 서버에서 상태를 관리하지 않으므로, 클라이언트에서 토큰 삭제
        return ResponseEntity.ok().build();
    }
}
