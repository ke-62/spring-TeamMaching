package com.sejong.recruit.dto;

import com.sejong.recruit.domain.project.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

public class CommentDto {

    @Getter
    @Builder
    public static class CreateRequest {
        private String content;
        private Boolean isSecret;
        private Long parentId;  // 대댓글이면 부모 댓글 id
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long projectId;
        private Long authorId;
        private String authorName;
        private String authorDepartment;
        private String content;      // 비밀글 + 권한 없으면 null
        private Boolean isSecret;
        private Boolean isVisible;   // 현재 요청자가 내용을 볼 수 있는지
        private String createdAt;
        private Long parentId;       // 대댓글이면 부모 댓글 id

        public static Response from(Comment comment, Long currentUserId, Long projectLeaderId) {
            boolean isVisible = !comment.getIsSecret()
                    || (currentUserId != null && comment.getAuthor().getId().equals(currentUserId))
                    || (currentUserId != null && currentUserId.equals(projectLeaderId));

            return Response.builder()
                    .id(comment.getId())
                    .projectId(comment.getProject().getId())
                    .authorId(comment.getAuthor().getId())
                    .authorName(comment.getAuthor().getFullName())
                    .authorDepartment(comment.getAuthor().getMajor())
                    .isSecret(comment.getIsSecret())
                    .isVisible(isVisible)
                    .content(isVisible ? comment.getContent() : null)
                    .createdAt(comment.getCreatedAt() != null
                            ? comment.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            : null)
                    .parentId(comment.getParentId())
                    .build();
        }
    }
}
