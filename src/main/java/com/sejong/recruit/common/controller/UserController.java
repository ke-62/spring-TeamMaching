package com.sejong.recruit.common.controller;

import com.sejong.recruit.dto.ApplicationDto;
import com.sejong.recruit.dto.UserDto;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.repository.UserRepository;
import com.sejong.recruit.service.ApplicationService;
import com.sejong.recruit.service.GitHubService;
import com.sejong.recruit.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GitHubService gitHubService;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @GetMapping("/me")
    public UserDto getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getMyProfile(userDetails.getUsername());
    }

    @GetMapping("/{userId}")
    public UserDto getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

    @PutMapping("/me")
    public UserDto updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestBody UpdateProfileRequest request) {
        return userService.updateMyProfile(
                userDetails.getUsername(),
                request.getName(),
                request.getTechStacks(),
                request.getInterests(),
                request.getGithubUrl(),
                request.getBio()
        );
    }

    @GetMapping("/me/applications")
    public List<ApplicationDto.Response> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
        return applicationService.getMyApplications(userDetails.getUsername());
    }

    @GetMapping("/me/projects")
    public List<?> getMyProjects(@AuthenticationPrincipal UserDetails userDetails) {
        return Collections.emptyList();
    }

    @GetMapping("/{userId}/github/profile")
    public GitHubService.GitHubProfile getGitHubProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return gitHubService.getProfile(user.getGithubUrl());
    }

    @GetMapping("/{userId}/github/repositories")
    public List<GitHubService.GitHubRepository> getGitHubRepositories(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return gitHubService.getRepositories(user.getGithubUrl(), limit);
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateProfileRequest {
        private String name;
        private List<String> techStacks;
        private List<String> interests;
        private String githubUrl;
        private String bio;
    }
}
