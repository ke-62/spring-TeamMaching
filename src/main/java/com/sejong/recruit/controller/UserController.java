package com.sejong.recruit.controller;

import com.sejong.recruit.dto.ApplicationDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.service.ApplicationService;
import com.sejong.recruit.service.UserService;
import com.sejong.recruit.service.GitHubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ApplicationService applicationService;
    private final GitHubService gitHubService;
    
    /**
     * 내 프로필 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // AuthController의 /auth/me 와 동일한 기능이지만 /users/me 경로로도 접근 가능하도록
        UserDto user = userService.getUserProfile(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok(user);
    }
    
    /**
     * 다른 사용자 프로필 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable Long userId) {
        UserDto user = userService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }
    
    /**
     * 내 프로필 수정
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserService.UpdateProfileRequest request
    ) {
        UserDto user = userService.updateMyProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(user);
    }
    
    /**
     * 내 지원 내역 조회
     */
    @GetMapping("/me/applications")
    public ResponseEntity<List<ApplicationDto.Response>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ApplicationDto.Response> applications = applicationService.getMyApplications(userDetails.getUsername());
        return ResponseEntity.ok(applications);
    }

    /**
     * GitHub 프로필 조회
     */
    @GetMapping("/{userId}/github/profile")
    public ResponseEntity<GitHubService.GitHubProfile> getGitHubProfile(@PathVariable Long userId) {
        UserDto user = userService.getUserProfile(userId);

        if (user.getGithubUrl() == null || user.getGithubUrl().isEmpty()) {
            throw new RuntimeException("GitHub URL이 등록되지 않았습니다");
        }

        String username = gitHubService.extractUsername(user.getGithubUrl());
        GitHubService.GitHubProfile profile = gitHubService.getProfile(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * GitHub 레포지토리 목록 조회
     */
    @GetMapping("/{userId}/github/repositories")
    public ResponseEntity<List<GitHubService.GitHubRepository>> getGitHubRepositories(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        UserDto user = userService.getUserProfile(userId);

        if (user.getGithubUrl() == null || user.getGithubUrl().isEmpty()) {
            throw new RuntimeException("GitHub URL이 등록되지 않았습니다");
        }

        String username = gitHubService.extractUsername(user.getGithubUrl());
        List<GitHubService.GitHubRepository> repositories = gitHubService.getRepositories(username, limit);
        return ResponseEntity.ok(repositories);
    }
}
