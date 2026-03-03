package com.sejong.recruit.common.controller;

import com.sejong.recruit.dto.CommentDto;
import com.sejong.recruit.service.CommentService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects/{projectId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping
    public List<CommentDto.Response> getComments(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentEmail = userDetails != null ? userDetails.getUsername() : null;
        return commentService.getComments(projectId, currentEmail);
    }

    // 댓글 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto.Response createComment(
            @PathVariable Long projectId,
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return commentService.createComment(
                projectId,
                userDetails.getUsername(),
                request.getContent(),
                request.getIsSecret(),
                request.getParentId()
        );
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long projectId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        commentService.deleteComment(projectId, commentId, userDetails.getUsername());
    }

    @Getter
    @NoArgsConstructor
    public static class CreateCommentRequest {
        private String content;
        private Boolean isSecret;
        private Long parentId;  // 대댓글이면 부모 댓글 id
    }
}
