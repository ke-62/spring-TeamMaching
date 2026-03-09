package com.sejong.recruit.service;

import com.sejong.recruit.dto.ApplicationDto;
import com.sejong.recruit.domain.recruitment.entity.Application;
import com.sejong.recruit.domain.recruitment.entity.ApplicationStatus;
import com.sejong.recruit.domain.project.entity.Project;
import com.sejong.recruit.domain.user.entity.User;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.repository.ApplicationRepository;
import com.sejong.recruit.repository.ProjectRepository;
import com.sejong.recruit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApplicationDto.Response applyToProject(Long projectId, String studentId, ApplicationDto.CreateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        User applicant = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (applicationRepository.existsByProjectIdAndApplicantId(projectId, applicant.getId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_APPLICATION);
        }

        Application application = Application.builder()
                .project(project)
                .applicant(applicant)
                .message(request.getMotivation())
                .build();

        application = applicationRepository.save(application);
        return ApplicationDto.Response.from(application);
    }

    @Transactional
    public void cancelApplication(Long applicationId, String studentId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!application.getApplicant().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        applicationRepository.delete(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDto.Response> getApplicationsByProject(Long projectId, String studentId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getLeader().getStudentId().equals(studentId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return applicationRepository.findByProjectId(projectId).stream()
                .map(ApplicationDto.Response::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationDto.Response> getMyApplications(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return applicationRepository.findByApplicantId(user.getId()).stream()
                .map(ApplicationDto.Response::from)
                .toList();
    }

    @Transactional
    public ApplicationDto.Response updateMemo(Long projectId, Long applicationId,
                                              String studentId, ApplicationDto.UpdateMemoRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getLeader().getStudentId().equals(studentId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        application.setMemo(request.getMemo());

        return ApplicationDto.Response.from(application);
    }

    @Transactional
    public ApplicationDto.Response updateApplicationStatus(Long projectId, Long applicationId,
                                                           String studentId, ApplicationDto.UpdateStatusRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getLeader().getStudentId().equals(studentId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        ApplicationStatus status = ApplicationStatus.valueOf(request.getStatus().toUpperCase());
        application.setStatus(status);

        return ApplicationDto.Response.from(application);
    }
}
