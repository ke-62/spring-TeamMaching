package com.sejong.recruit.service;

import com.sejong.recruit.dto.ProjectDto;
import com.sejong.recruit.domain.project.entity.Project;
import com.sejong.recruit.domain.project.entity.ProjectStatus;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.repository.ProjectRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getProjects(String status, String search, String role,
                                           String projectType, String techStack, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Project> projectPage;

        if (techStack != null && !techStack.isBlank()) {
            projectPage = projectRepository.findByTechStack(techStack, pageable);
        } else {
            projectPage = projectRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<ProjectDto.Response> content = projectPage.getContent().stream()
                .map(ProjectDto.Response::from)
                .filter(r -> {
                    // 상태 필터
                    if (status != null && !status.isBlank() && !r.getStatus().equalsIgnoreCase(status)) {
                        return false;
                    }
                    // 프로젝트 타입 필터
                    if (projectType != null && !projectType.isBlank() && !r.getProjectType().equals(projectType)) {
                        return false;
                    }
                    // 검색어 필터 (제목 + 설명)
                    if (search != null && !search.isBlank()) {
                        String searchLower = search.toLowerCase();
                        boolean matchTitle = r.getTitle().toLowerCase().contains(searchLower);
                        boolean matchDesc = r.getDescription().toLowerCase().contains(searchLower);
                        if (!matchTitle && !matchDesc) {
                            return false;
                        }
                    }
                    // 역할(기술스택) 필터
                    if (role != null && !role.isBlank()) {
                        return r.getRequiredTechStacks().stream()
                                .anyMatch(tech -> tech.equalsIgnoreCase(role));
                    }
                    return true;
                })
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalPages", projectPage.getTotalPages());
        result.put("totalElements", content.size());
        return result;
    }

    @Transactional(readOnly = true)
    public ProjectDto.Response getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));
        return ProjectDto.Response.from(project);
    }

    @Transactional
    public ProjectDto.Response createProject(String studentId, ProjectDto.CreateRequest request) {
        User leader = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String requiredRoles = encodeMetadata(request);

        LocalDateTime deadline = null;
        if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
            deadline = LocalDateTime.parse(request.getDeadline());
        }

        Project project = Project.builder()
                .leader(leader)
                .title(request.getTitle())
                .content(request.getDescription())
                .requiredRoles(requiredRoles)
                .deadline(deadline)
                .status(ProjectStatus.RECRUITING)
                .build();

        project = projectRepository.save(project);
        return ProjectDto.Response.from(project);
    }

    @Transactional
    public ProjectDto.Response updateProject(Long id, String studentId, ProjectDto.UpdateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getLeader().getStudentId().equals(studentId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (request.getTitle() != null) project.setTitle(request.getTitle());
        if (request.getDescription() != null) project.setContent(request.getDescription());
        project.setRequiredRoles(encodeMetadataFromUpdate(request));
        if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
            project.setDeadline(LocalDateTime.parse(request.getDeadline()));
        }

        return ProjectDto.Response.from(project);
    }

    @Transactional
    public ProjectDto.Response updateProjectStatus(Long id, String studentId, ProjectDto.StatusUpdateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getLeader().getStudentId().equals(studentId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            ProjectStatus newStatus = ProjectStatus.valueOf(request.getStatus());
            project.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return ProjectDto.Response.from(project);
    }

    @Transactional
    public void deleteProject(Long id, String studentId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getLeader().getStudentId().equals(studentId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        projectRepository.delete(project);
    }

    private String encodeMetadata(ProjectDto.CreateRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("projectType:").append(request.getProjectType() != null ? request.getProjectType() : "other");
        sb.append("|recruitNumber:").append(request.getRecruitNumber() != null ? request.getRecruitNumber() : 1);
        if (request.getRequiredTechStacks() != null && !request.getRequiredTechStacks().isEmpty()) {
            sb.append("|techStacks:").append(String.join(",", request.getRequiredTechStacks()));
        }
        return sb.toString();
    }

    private String encodeMetadataFromUpdate(ProjectDto.UpdateRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("projectType:").append(request.getProjectType() != null ? request.getProjectType() : "other");
        sb.append("|recruitNumber:").append(request.getRecruitNumber() != null ? request.getRecruitNumber() : 1);
        if (request.getRequiredTechStacks() != null && !request.getRequiredTechStacks().isEmpty()) {
            sb.append("|techStacks:").append(String.join(",", request.getRequiredTechStacks()));
        }
        return sb.toString();
    }
}
