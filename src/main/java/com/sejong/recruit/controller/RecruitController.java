package com.sejong.recruit.controller;

import com.sejong.recruit.dto.RecruitDto;
import com.sejong.recruit.service.RecruitService;
import com.sejong.recruit.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
public class RecruitController {

    private final RecruitService recruitService;
    private final RecommendationService recommendationService;
    
    /**
     * 모집 공고 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<RecruitDto.Response>> getRecruitPosts(
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String techStack,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RecruitDto.Response> posts = recruitService.getRecruitPosts(projectType, techStack, pageable);
        return ResponseEntity.ok(posts);
    }
    
    /**
     * 모집 공고 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecruitDto.Response> getRecruitPost(@PathVariable Long id) {
        RecruitDto.Response post = recruitService.getRecruitPost(id);
        return ResponseEntity.ok(post);
    }
    
    /**
     * 모집 공고 작성
     */
    @PostMapping
    public ResponseEntity<RecruitDto.Response> createRecruitPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RecruitDto.CreateRequest request
    ) {
        RecruitDto.Response post = recruitService.createRecruitPost(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    /**
     * 모집 공고 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecruitDto.Response> updateRecruitPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RecruitDto.UpdateRequest request
    ) {
        RecruitDto.Response post = recruitService.updateRecruitPost(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(post);
    }
    
    /**
     * 모집 공고 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecruitPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        recruitService.deleteRecruitPost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    /**
     * AI 기반 팀원 추천
     */
    @GetMapping("/{id}/recommendations")
    public ResponseEntity<List<RecommendationService.RecommendedUserDto>> getRecommendations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecommendationService.RecommendedUserDto> recommendations =
                recommendationService.recommendUsers(id, limit);
        return ResponseEntity.ok(recommendations);
    }
}
