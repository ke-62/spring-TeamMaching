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
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final RecommendationService recommendationService;
    private final ApplicationService applicationService;

    // 프로젝트 생성
    @PostMapping
    public ProjectDto.Response createProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectDto.CreateRequest request) {
        return projectService.createProject(userDetails.getUsername(), request);
    }

    // 모집 목록 조회 (필터링 가능)
    @GetMapping
    public Map<String, Object> getProjects(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String techStack,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return projectService.getProjects(status, search, role, projectType, techStack, page, size);
    }

    // 공고 상세 조회
    @GetMapping("/{id}")
    public ProjectDto.Response getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    // 프로젝트 상태 변경 (모집중 -> 진행중 -> 완료)
    @PatchMapping("/{id}")
    public ProjectDto.Response updateProjectStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectDto.StatusUpdateRequest request) {
        return projectService.updateProjectStatus(id, userDetails.getUsername(), request);
    }

    // 프로젝트 수정 (전체)
    @PutMapping("/{id}")
    public ProjectDto.Response updateProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProjectDto.UpdateRequest request) {
        return projectService.updateProject(id, userDetails.getUsername(), request);
    }

    // 프로젝트 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        projectService.deleteProject(id, userDetails.getUsername());
        return ApiResponse.success();
    }

    // 지원하기
    @PostMapping("/{id}/apply")
    public ApplicationDto.Response applyToProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ApplicationDto.CreateRequest request) {
        return applicationService.applyToProject(id, userDetails.getUsername(), request);
    }

    // AI 추천 팀원 조회
    @GetMapping("/{id}/recommendations")
    public List<RecommendationService.RecommendedUserDto> getRecommendations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        return recommendationService.recommendUsers(id, limit);
    }
}
