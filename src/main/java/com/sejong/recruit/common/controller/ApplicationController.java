package com.sejong.recruit.common.controller;

import com.sejong.recruit.dto.ApplicationDto;
import com.sejong.recruit.common.response.ApiResponse;
import com.sejong.recruit.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects/{projectId}/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ApplicationDto.Response apply(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ApplicationDto.CreateRequest request) {
        return applicationService.applyToProject(projectId, userDetails.getUsername(), request);
    }

    @GetMapping
    public List<ApplicationDto.Response> getApplications(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return applicationService.getApplicationsByProject(projectId, userDetails.getUsername());
    }

    @PatchMapping("/{applicationId}")
    public ApplicationDto.Response updateStatus(
            @PathVariable Long projectId,
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ApplicationDto.UpdateStatusRequest request) {
        return applicationService.updateApplicationStatus(projectId, applicationId,
                userDetails.getUsername(), request);
    }

    @DeleteMapping("/{applicationId}")
    public ApiResponse<Void> cancelApplication(
            @PathVariable Long projectId,
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        applicationService.cancelApplication(applicationId, userDetails.getUsername());
        return ApiResponse.success();
    }
}
