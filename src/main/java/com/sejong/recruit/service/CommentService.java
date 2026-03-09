package com.sejong.recruit.service;

import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.domain.project.entity.Comment;
import com.sejong.recruit.domain.project.entity.Project;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.dto.CommentDto;
import com.sejong.recruit.repository.CommentRepository;
import com.sejong.recruit.repository.ProjectRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 댓글 목록 조회 (비밀글 필터링 포함)
    @Transactional(readOnly = true)
    public List<CommentDto.Response> getComments(Long projectId, String currentUserStudentId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        Long leaderId = project.getLeader().getId();

        // 로그인한 경우 currentUserId 조회, 비로그인은 null
        Long currentUserId = null;
        if (currentUserStudentId != null) {
            currentUserId = userRepository.findByStudentId(currentUserStudentId)
                    .map(User::getId)
                    .orElse(null);
        }

        final Long finalCurrentUserId = currentUserId;
        return commentRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
                .stream()
                .map(comment -> CommentDto.Response.from(comment, finalCurrentUserId, leaderId))
                .collect(Collectors.toList());
    }

    // 댓글 등록 (parentId 있으면 대댓글)
    @Transactional
    public CommentDto.Response createComment(Long projectId, String studentId, String content, Boolean isSecret, Long parentId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        User author = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .project(project)
                .author(author)
                .content(content)
                .isSecret(isSecret != null ? isSecret : false)
                .parentId(parentId)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentDto.Response.from(saved, author.getId(), project.getLeader().getId());
    }

    // 댓글 삭제 (본인 또는 프로젝트 리더만)
    @Transactional
    public void deleteComment(Long projectId, Long commentId, String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        Project project = comment.getProject();
        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());

        if (!isAuthor) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        commentRepository.delete(comment);
    }
}
