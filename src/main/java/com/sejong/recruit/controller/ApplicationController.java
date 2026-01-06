package com.sejong.recruit.controller;

import com.sejong.recruit.dto.ApplicationDto;
import com.sejong.recruit.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruits/{recruitPostId}/applications")
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    /**
     * 지원하기
     */
    @PostMapping
    public ResponseEntity<ApplicationDto.Response> applyToRecruit(
            @PathVariable Long recruitPostId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplicationDto.CreateRequest request
    ) {
        ApplicationDto.Response application = applicationService.applyToRecruit(
                recruitPostId,
                userDetails.getUsername(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }
    
    /**
     * 지원자 목록 조회 (공고 작성자용)
     */
    @GetMapping
    public ResponseEntity<List<ApplicationDto.Response>> getApplications(
            @PathVariable Long recruitPostId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ApplicationDto.Response> applications = applicationService.getApplicationsByRecruitPost(
                recruitPostId,
                userDetails.getUsername()
        );
        return ResponseEntity.ok(applications);
    }
    
    /**
     * 지원 상태 변경 (수락/거절)
     */
    @PatchMapping("/{applicationId}")
    public ResponseEntity<ApplicationDto.Response> updateApplicationStatus(
            @PathVariable Long recruitPostId,
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplicationDto.UpdateStatusRequest request
    ) {
        ApplicationDto.Response application = applicationService.updateApplicationStatus(
                recruitPostId,
                applicationId,
                userDetails.getUsername(),
                request
        );
        return ResponseEntity.ok(application);
    }
    
    /**
     * 지원 취소
     */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> cancelApplication(
            @PathVariable Long recruitPostId,
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        applicationService.cancelApplication(applicationId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
