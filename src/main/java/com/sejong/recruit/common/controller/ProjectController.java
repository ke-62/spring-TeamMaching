package com.sejong.recruit.common.controller;

import com.sejong.recruit.dto.ApplicationDto;
import com.sejong.recruit.dto.ProjectDto;
import com.sejong.recruit.common.response.ApiResponse;
import com.sejong.recruit.service.ApplicationService;
import com.sejong.recruit.service.ProjectService;
import com.sejong.recruit.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final RecommendationService recommendationService;
    private final ApplicationService applicationService;

    @GetMapping
    public Map<String, Object> getRecruitPosts(
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String techStack,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return projectService.getProjects(projectType, techStack, page, size);
    }

    @GetMapping("/{id}")
    public ProjectDto.Response getRecruitPost(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PostMapping
    public ProjectDto.Response createRecruitPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectDto.CreateRequest request) {
        return projectService.createProject(userDetails.getUsername(), request);
    }

    @PutMapping("/{id}")
    public ProjectDto.Response updateRecruitPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectDto.UpdateRequest request) {
        return projectService.updateProject(id, userDetails.getUsername(), request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecruitPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        projectService.deleteProject(id, userDetails.getUsername());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/apply")
    public ApplicationDto.Response applyToRecruit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ApplicationDto.CreateRequest request) {
        return applicationService.applyToProject(id, userDetails.getUsername(), request);
    }

    @GetMapping("/{id}/recommendations")
    public List<RecommendationService.RecommendedUserDto> getRecommendations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        return recommendationService.recommendUsers(id, limit);
    }
}
